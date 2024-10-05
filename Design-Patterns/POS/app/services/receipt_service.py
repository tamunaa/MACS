from uuid import UUID

from app.domain.models import Receipt
from app.repositories.receipt_repository import ReceiptRepository


class ReceiptService:
    def __init__(self, receipt_repo: ReceiptRepository):
        self.receipt_repo = receipt_repo

    def create_receipt(self) -> Receipt:
        return self.receipt_repo.create()

    def get_receipt_by_id(self, id: UUID) -> Receipt:
        return self.receipt_repo.get_by_id(id)

    def add_product(self, receipt_id: UUID, product_id: UUID, quantity: int) -> Receipt:
        return self.receipt_repo.add_product(receipt_id, product_id, quantity)

    def close_receipt(self, id: UUID) -> None:
        return self.receipt_repo.close(id)

    def delete_receipt(self, id: UUID) -> None:
        return self.receipt_repo.delete(id)
