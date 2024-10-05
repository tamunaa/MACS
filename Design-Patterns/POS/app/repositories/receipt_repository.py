from decimal import Decimal
from sqlite3 import Connection
from typing import Any, Callable, List, Optional
from uuid import UUID

from ..domain.exceptions import ForbiddenError, NotFoundError
from ..domain.models import Receipt, ReceiptProduct
from .base import BaseRepository
from .product_repository import ProductRepository


class ReceiptRepository(BaseRepository[Receipt]):
    def __init__(
            self,
            connection_factory: Callable[[], Connection],
            product_repo: ProductRepository,
    ) -> None:
        super().__init__(connection_factory)
        self.product_repository = product_repo

    def _to_model(
            self,
            row: Optional[dict[str, Any]],
            products: Optional[List[ReceiptProduct]] = None,
    ) -> Optional[Receipt]:
        if not row:
            return None
        return Receipt(
            id=UUID(row["id"]),
            status=row["status"],
            products=products or [],
            total=Decimal(str(row["total"])),
        )

    def create(self) -> Receipt:
        receipt_id = UUID(self.generate_id())
        self.execute_query(
            """INSERT INTO receipts (id, status, total) 
               VALUES (?, ?, ?)""",
            (str(receipt_id), "open", "0"),
        )
        return self.get_by_id(receipt_id)

    def get_by_id(self, id: UUID) -> Receipt:
        row = self.fetch_one("SELECT r.* FROM receipts r WHERE r.id = ?", (str(id),))
        if not row:
            raise NotFoundError(f"Receipt with id<{id}> does not exist.")

        products = self.fetch_all(
            """SELECT rp.*, p.price 
               FROM receipt_products rp
               JOIN products p ON p.id = rp.product_id 
               WHERE rp.receipt_id = ?""",
            (str(id),),
        )

        receipt_products = [
            ReceiptProduct(
                id=UUID(p["product_id"]),
                quantity=p["quantity"],
                price=Decimal(str(p["price"])),
                total=Decimal(str(p["total"])),
            )
            for p in products
        ]

        result = self._to_model(row, receipt_products)
        if result is None:
            raise NotFoundError(f"Receipt with id<{id}> does not exist.")
        return result

    def add_product(self, receipt_id: UUID, product_id: UUID, quantity: int) -> Receipt:
        receipt = self.get_by_id(receipt_id)
        if receipt.status == "closed":
            raise ForbiddenError(f"Receipt with id<{receipt_id}> is closed.")

        product = self.product_repository.get_by_id(product_id)
        total = product.price * Decimal(str(quantity))

        self.execute_query(
            """INSERT INTO receipt_products (receipt_id, product_id, quantity, total)
               VALUES (?, ?, ?, ?)""",
            (str(receipt_id), str(product_id), quantity, str(total)),
        )

        new_total = receipt.total + total
        self.execute_query(
            "UPDATE receipts SET total = ? WHERE id = ?",
            (str(new_total), str(receipt_id)),
        )

        return self.get_by_id(receipt_id)

    def close(self, id: UUID) -> None:
        if not self.exists("SELECT 1 FROM receipts WHERE id = ?", (str(id),)):
            raise NotFoundError(f"Receipt with id<{id}> does not exist.")

        self.execute_query(
            "UPDATE receipts SET status = ? WHERE id = ?", ("closed", str(id))
        )

    def delete(self, id: UUID) -> None:
        receipt = self.get_by_id(id)
        if receipt.status == "closed":
            raise ForbiddenError(f"Receipt with id<{id}> is closed.")

        self.execute_query("DELETE FROM receipts WHERE id = ?", (str(id),))
