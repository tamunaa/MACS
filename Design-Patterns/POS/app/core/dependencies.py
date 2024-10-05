import sqlite3
from typing import Callable

from fastapi import Depends

from app.core.db import get_db_connection
from app.repositories.product_repository import ProductRepository
from app.repositories.receipt_repository import ReceiptRepository
from app.repositories.sales_repository import SalesRepository
from app.repositories.unit_repository import UnitRepository
from app.services.product_service import ProductService
from app.services.receipt_service import ReceiptService
from app.services.sales_service import SalesService
from app.services.unit_service import UnitService


def get_unit_repository(
    db: Callable[[], sqlite3.Connection] = Depends(get_db_connection),
) -> UnitRepository:
    return UnitRepository(db)


def get_unit_service(
    repo: UnitRepository = Depends(get_unit_repository),
) -> UnitService:
    return UnitService(repo)


def get_product_repository(
    db: Callable[[], sqlite3.Connection] = Depends(get_db_connection),
) -> ProductRepository:
    return ProductRepository(db, unit_repo=get_unit_repository(db))


def get_product_service() -> ProductService:
    return ProductService(product_repo=get_product_repository())


def get_receipt_repository(
    db: Callable[[], sqlite3.Connection] = Depends(get_db_connection),
) -> ReceiptRepository:
    return ReceiptRepository(db, product_repo=get_product_repository(db))


def get_receipt_service(
    repo: ReceiptRepository = Depends(get_receipt_repository),
) -> ReceiptService:
    return ReceiptService(repo)


def get_sales_repository(
    db: Callable[[], sqlite3.Connection] = Depends(get_db_connection),
) -> SalesRepository:
    return SalesRepository(db)


def get_sales_service(
    repo: SalesRepository = Depends(get_sales_repository),
) -> SalesService:
    return SalesService(repo)
