from ..domain.models import Sales
from ..repositories.sales_repository import SalesRepository


class SalesService:
    def __init__(self, sales_repo: SalesRepository):
        self.sales_repo = sales_repo

    def get_sales_report(self) -> Sales:
        return self.sales_repo.get_report()
