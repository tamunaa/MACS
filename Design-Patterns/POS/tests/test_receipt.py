from decimal import Decimal
from typing import Generator
from unittest.mock import Mock
from uuid import UUID

import pytest
from fastapi.testclient import TestClient

from app.core.dependencies import get_receipt_service, get_sales_service
from app.domain.exceptions import ForbiddenError, NotFoundError
from app.domain.models import Product, Receipt, ReceiptProduct, Sales
from app.services.receipt_service import ReceiptService
from app.services.sales_service import SalesService
from main import app

SAMPLE_PRODUCT: Product = Product(
    id=UUID("7d3184ae-80cd-417f-8b14-e3de42a98031"),
    unit_id=UUID("123e4567-e89b-12d3-a456-426614174000"),
    name="Test Product",
    barcode="123456789",
    price=Decimal("520"),
)

SAMPLE_RECEIPT: Receipt = Receipt(
    id=UUID("25f13441-5fab-4b12-aefe-3fa0089fb63a"),
    status="open",
    products=[
        ReceiptProduct(
            id=SAMPLE_PRODUCT.id,
            quantity=123,
            price=SAMPLE_PRODUCT.price,
            total=Decimal("63960"),
        )
    ],
    total=Decimal("63960"),
)

SAMPLE_SALES: Sales = Sales(n_receipts=23, revenue=Decimal("456890"))


@pytest.fixture
def mock_receipt_service() -> Mock:
    return Mock(spec=ReceiptService)


@pytest.fixture
def mock_sales_service() -> Mock:
    return Mock(spec=SalesService)


@pytest.fixture
def client_with_mocked_services(
        mock_receipt_service: Mock, mock_sales_service: Mock
) -> Generator[TestClient, None, None]:
    app.dependency_overrides[get_receipt_service] = lambda: mock_receipt_service
    app.dependency_overrides[get_sales_service] = lambda: mock_sales_service
    yield TestClient(app)
    app.dependency_overrides = {}


def test_create_receipt(
        client_with_mocked_services: TestClient, mock_receipt_service: Mock
) -> None:
    empty_receipt: Receipt = Receipt(
        id=UUID("25f13441-5fab-4b12-aefe-3fa0089fb63a"),
        status="open",
        products=[],
        total=Decimal("0"),
    )
    mock_receipt_service.create_receipt.return_value = empty_receipt

    response = client_with_mocked_services.post("/receipts")

    assert response.status_code == 201
    assert response.json() == {
        "receipt": {
            "id": "25f13441-5fab-4b12-aefe-3fa0089fb63a",
            "status": "open",
            "products": [],
            "total": "0",
        }
    }
    mock_receipt_service.create_receipt.assert_called_once()


def test_add_product_to_receipt(
        client_with_mocked_services: TestClient, mock_receipt_service: Mock
) -> None:
    mock_receipt_service.add_product.return_value = SAMPLE_RECEIPT
    product_data: dict[str, int | str] = {
        "id": "7d3184ae-80cd-417f-8b14-e3de42a98031",
        "quantity": 123,
    }

    response = client_with_mocked_services.post(
        f"/receipts/{SAMPLE_RECEIPT.id}/products", json=product_data
    )

    assert response.status_code == 201
    assert response.json() == {
        "receipt": {
            "id": str(SAMPLE_RECEIPT.id),
            "status": "open",
            "products": [
                {
                    "id": str(SAMPLE_PRODUCT.id),
                    "quantity": 123,
                    "price": "520",
                    "total": "63960",
                }
            ],
            "total": "63960",
        }
    }
    mock_receipt_service.add_product.assert_called_once_with(
        receipt_id=SAMPLE_RECEIPT.id,
        product_id=UUID(str(product_data["id"])),
        quantity=product_data["quantity"],
    )


def test_get_receipt(
        client_with_mocked_services: TestClient, mock_receipt_service: Mock
) -> None:
    mock_receipt_service.get_receipt_by_id.return_value = SAMPLE_RECEIPT

    response = client_with_mocked_services.get(f"/receipts/{SAMPLE_RECEIPT.id}")

    assert response.status_code == 200
    assert response.json() == {
        "receipt": {
            "id": str(SAMPLE_RECEIPT.id),
            "status": "open",
            "products": [
                {
                    "id": str(SAMPLE_PRODUCT.id),
                    "quantity": 123,
                    "price": "520",
                    "total": "63960",
                }
            ],
            "total": "63960",
        }
    }
    mock_receipt_service.get_receipt_by_id.assert_called_once_with(SAMPLE_RECEIPT.id)


def test_get_receipt_not_found(
        client_with_mocked_services: TestClient, mock_receipt_service: Mock
) -> None:
    mock_receipt_service.get_receipt_by_id.side_effect = NotFoundError(
        "Receipt with id<25f13441-5fab-4b12-aefe-3fa0089fb63a> does not exist."
    )

    response = client_with_mocked_services.get(f"/receipts/{SAMPLE_RECEIPT.id}")

    assert response.status_code == 404
    assert response.json() == {
        "detail": {
            "message": "Receipt with id<25f13441-5fab-4b12-aefe-3fa0089fb63a> does not "
                       "exist."
        }
    }


def test_close_receipt(
        client_with_mocked_services: TestClient, mock_receipt_service: Mock
) -> None:
    status_update: dict[str, str] = {"status": "closed"}

    response = client_with_mocked_services.patch(
        f"/receipts/{SAMPLE_RECEIPT.id}", json=status_update
    )

    assert response.status_code == 200
    assert response.json() == {}
    mock_receipt_service.close_receipt.assert_called_once_with(SAMPLE_RECEIPT.id)


def test_close_receipt_invalid_status(
        client_with_mocked_services: TestClient, mock_receipt_service: Mock
) -> None:
    status_update: dict[str, str] = {"status": "invalid"}

    response = client_with_mocked_services.patch(
        f"/receipts/{SAMPLE_RECEIPT.id}", json=status_update
    )

    assert response.status_code == 400
    assert response.json() == {"detail": {"message": "Invalid status update"}}
    mock_receipt_service.close_receipt.assert_not_called()


def test_delete_closed_receipt(
        client_with_mocked_services: TestClient, mock_receipt_service: Mock
) -> None:
    mock_receipt_service.delete_receipt.side_effect = ForbiddenError(
        "Receipt with id<25f13441-5fab-4b12-aefe-3fa0089fb63a> is closed."
    )

    response = client_with_mocked_services.delete(f"/receipts/{SAMPLE_RECEIPT.id}")

    assert response.status_code == 403
    assert response.json() == {
        "detail": {
            "message": "Receipt with id<25f13441-5fab-4b12-aefe-3fa0089fb63a> is "
                       "closed."
        }
    }


def test_get_sales_report(
        client_with_mocked_services: TestClient, mock_sales_service: Mock
) -> None:
    mock_sales_service.get_sales_report.return_value = SAMPLE_SALES

    response = client_with_mocked_services.get("/sales")

    assert response.status_code == 200
    assert response.json() == {"sales": {"n_receipts": 23, "revenue": "456890"}}
    mock_sales_service.get_sales_report.assert_called_once()
