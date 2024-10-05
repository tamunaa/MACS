from decimal import Decimal
from typing import Any, List, Optional
from uuid import UUID

from pydantic import BaseModel, Field

from app.domain.models import Product, Receipt, Sales, Unit


class UnitBase(BaseModel):
    name: str


class UnitCreate(UnitBase):
    name: str
    pass


class UnitResponse(BaseModel):
    unit: Unit


class UnitsListResponse(BaseModel):
    units: List[Unit]


class ErrorResponse(BaseModel):
    error: dict[str, Any]


class ProductBase(BaseModel):
    name: str
    barcode: str
    unit_id: UUID
    price: Decimal = Field(ge=0)


class ProductCreate(ProductBase):
    pass


class ProductUpdate(BaseModel):
    name: Optional[str] = None
    barcode: Optional[str] = None
    unit_id: Optional[UUID] = None
    price: Optional[Decimal] = Field(default=None, ge=0)


class ProductResponse(BaseModel):
    product: Product


class ProductsListResponse(BaseModel):
    products: List[Product]


class ReceiptProductBase(BaseModel):
    id: UUID
    quantity: int
    price: Decimal
    total: Decimal


class ReceiptProductCreate(BaseModel):
    id: UUID
    quantity: int


class ReceiptBase(BaseModel):
    id: UUID
    status: str
    products: List[ReceiptProductBase]
    total: Decimal


class ReceiptResponse(BaseModel):
    receipt: Receipt


class ReceiptStatusUpdate(BaseModel):
    status: str


class SalesBase(BaseModel):
    n_receipts: int
    revenue: Decimal


class SalesResponse(BaseModel):
    sales: Sales
