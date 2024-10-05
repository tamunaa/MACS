from dataclasses import dataclass
from decimal import Decimal
from enum import Enum
from typing import List
from uuid import UUID


class ReceiptStatus(str, Enum):
    OPEN = "open"
    CLOSED = "closed"


@dataclass
class Unit:
    id: UUID
    name: str


@dataclass
class Product:
    id: UUID
    unit_id: UUID
    name: str
    barcode: str
    price: Decimal


@dataclass
class ReceiptProduct:
    id: UUID
    quantity: int
    price: Decimal
    total: Decimal


@dataclass
class Receipt:
    id: UUID
    status: str  # "open" or "closed"
    products: List[ReceiptProduct]
    total: Decimal


@dataclass
class Sales:
    n_receipts: int
    revenue: Decimal
