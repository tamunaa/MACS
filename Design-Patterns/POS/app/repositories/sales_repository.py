from decimal import Decimal
from sqlite3 import Connection
from typing import Callable

from app.domain.models import Sales
from app.repositories.base import BaseRepository


class SalesRepository(BaseRepository[Sales]):
    def __init__(self, connection_factory: Callable[[], Connection]) -> None:
        super().__init__(connection_factory)

    def get_report(self) -> Sales:
        row = self.fetch_one(
            """SELECT COUNT(*) as n_receipts, COALESCE(SUM(total), 0) as revenue 
               FROM receipts 
               WHERE status = 'closed'"""
        )

        if not row:
            return Sales(n_receipts=0, revenue=Decimal("0.00"))

        return Sales(n_receipts=row["n_receipts"], revenue=Decimal(str(row["revenue"])))
