from decimal import Decimal
from sqlite3 import Connection
from typing import Any, Callable, List
from uuid import UUID

from ..domain.exceptions import DuplicateError, NotFoundError
from ..domain.models import Product
from .base import BaseRepository
from .unit_repository import UnitRepository


class ProductRepository(BaseRepository[Product]):
    def __init__(
        self, connection_factory: Callable[[], Connection], unit_repo: UnitRepository
    ) -> None:
        super().__init__(connection_factory)
        self.unit_repository = unit_repo

    def _to_model(self, row: dict[str, Any]) -> Product:
        return Product(
            id=UUID(row["id"]),
            unit_id=UUID(row["unit_id"]),
            name=row["name"],
            barcode=row["barcode"],
            price=Decimal(str(row["price"])),
        )

    def create(self, unit_id: UUID, name: str, barcode: str, price: Decimal) -> Product:
        if self.exists("SELECT 1 FROM products WHERE barcode = ?", (barcode,)):
            raise DuplicateError(f"Product with barcode<{barcode}> already exists.")

        res = self.unit_repository.get_by_id(unit_id)

        if not res:
            raise NotFoundError(f"Unit with id<{unit_id}> does not exist.")

        product_id = self.generate_id()
        self.execute_query(
            """INSERT INTO products (id, unit_id, name, barcode, price) 
               VALUES (?, ?, ?, ?, ?)""",
            (str(product_id), str(unit_id), name, barcode, str(price)),
        )

        product = self.get_by_id(UUID(product_id))
        if not product:
            raise ValueError("Failed to create product")
        return product

    def get_by_id(self, id: UUID) -> Product:
        row = self.fetch_one("SELECT * FROM products WHERE id = ?", (str(id),))
        if not row:
            raise NotFoundError(f"Product with id<{id}> does not exist.")

        product = self._to_model(row)
        return product

    def get_all(self) -> List[Product]:
        rows = self.fetch_all("SELECT * FROM products")
        return [self._to_model(row) for row in rows if row]

    def update(self, id: UUID, updates: dict[str, Any]) -> Product:
        if not self.exists("SELECT 1 FROM products WHERE id = ?", (str(id),)):
            raise NotFoundError(f"Product with id<{id}> does not exist.")

        if "barcode" in updates:
            if self.exists(
                "SELECT 1 FROM products WHERE barcode = ? AND id != ?",
                (updates["barcode"], str(id)),
            ):
                raise DuplicateError(
                    f"Product with barcode<{updates['barcode']}> already exists."
                )

        set_clause = ", ".join(f"{k} = ?" for k in updates.keys())
        query = f"UPDATE products SET {set_clause} WHERE id = ?"

        values = []
        for v in updates.values():
            if isinstance(v, UUID):
                values.append(str(v))
            elif isinstance(v, Decimal):
                values.append(str(v))
            else:
                values.append(v)
        values.append(str(id))

        self.execute_query(query, tuple(values))
        return self.get_by_id(id)
