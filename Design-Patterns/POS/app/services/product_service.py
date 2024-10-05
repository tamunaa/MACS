from decimal import Decimal
from typing import Any, Dict, List
from uuid import UUID

from app.domain.models import Product
from app.repositories.product_repository import ProductRepository


class ProductService:
    def __init__(self, product_repo: ProductRepository):
        self.product_repo = product_repo

    def create_product(
        self, unit_id: UUID, name: str, barcode: str, price: Decimal
    ) -> Product:
        return self.product_repo.create(unit_id, name, barcode, price)

    def get_product_by_id(self, id: UUID) -> Product:
        return self.product_repo.get_by_id(id)

    def get_all_products(self) -> List[Product]:
        return self.product_repo.get_all()

    def update_product(self, id: UUID, updates: Dict[str, Any]) -> Product:
        return self.product_repo.update(id, updates)
