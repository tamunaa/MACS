from typing import List
from uuid import UUID

from app.domain.models import Unit
from app.repositories.unit_repository import UnitRepository


class UnitService:
    def __init__(self, unit_repo: UnitRepository):
        self.unit_repo = unit_repo

    def create_unit(self, name: str) -> Unit:
        return self.unit_repo.create(name)

    def get_unit_by_id(self, id: UUID) -> Unit:
        return self.unit_repo.get_by_id(id)

    def get_all_units(self) -> List[Unit]:
        return self.unit_repo.get_all()

    def update_unit(self, id: UUID, name: str) -> Unit:
        return self.unit_repo.update(id, name)

    def delete_unit(self, id: UUID) -> bool:
        return self.unit_repo.delete(id)
