from sqlite3 import Connection
from typing import Callable, List
from uuid import UUID

from ..domain.exceptions import DuplicateError, NotFoundError
from ..domain.models import Unit
from .base import BaseRepository


class UnitRepository(BaseRepository[Unit]):
    def __init__(self, connection_factory: Callable[[], Connection]) -> None:
        super().__init__(connection_factory)

    def _to_model(self, row: dict[str, str]) -> Unit:
        return Unit(id=UUID(row["id"]), name=row["name"])

    def create(self, name: str) -> Unit:
        if self.exists("SELECT 1 FROM units WHERE name = ?", (name,)):
            raise DuplicateError(f"Unit with name<{name}> already exists.")

        unit_id = self.generate_id()
        self.execute_query(
            "INSERT INTO units (id, name) VALUES (?, ?)", (unit_id, name)
        )

        unit = self.get_by_id(UUID(unit_id))
        if not unit:
            raise ValueError("Failed to create unit")
        return unit

    def get_by_id(self, id: UUID) -> Unit:
        row = self.fetch_one("SELECT * FROM units WHERE id = ?", (str(id),))
        if not row:
            raise NotFoundError(f"Unit with id<{id}> does not exist.")
        unit = self._to_model(row)
        return unit

    def get_all(self) -> List[Unit]:
        rows = self.fetch_all("SELECT * FROM units")
        return [self._to_model(row) for row in rows]

    def update(self, id: UUID, name: str) -> Unit:
        if not self.exists("SELECT 1 FROM units WHERE id = ?", (str(id),)):
            raise NotFoundError(f"Unit with id<{id}> does not exist.")

        if self.exists(
            "SELECT 1 FROM units WHERE name = ? AND id != ?", (name, str(id))
        ):
            raise DuplicateError(f"Unit with name<{name}> already exists.")

        self.execute_query("UPDATE units SET name = ? WHERE id = ?", (name, str(id)))
        return self.get_by_id(id)

    def delete(self, id: UUID) -> bool:
        if not self.exists("SELECT 1 FROM units WHERE id = ?", (str(id),)):
            raise NotFoundError(f"Unit with id<{id}> does not exist.")

        with self.transaction():
            if self.exists("SELECT 1 FROM products WHERE unit_id = ?", (str(id),)):
                raise ValueError("Cannot delete unit that is being used by products")

            self.execute_query("DELETE FROM units WHERE id = ?", (str(id),))
            return True
