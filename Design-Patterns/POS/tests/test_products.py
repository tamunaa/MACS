from decimal import Decimal
from typing import Generator
from unittest.mock import Mock
from uuid import UUID

import pytest
from fastapi.testclient import TestClient

from app.core.dependencies import get_product_service
from app.domain.exceptions import DuplicateError, NotFoundError
from app.domain.models import Product, Unit
from app.services.product_service import ProductService
from main import app

SAMPLE_UNIT: Unit = Unit(
    id=UUID("123e4567-e89b-12d3-a456-426614174000"), name="Kilogram"
)

SAMPLE_PRODUCT: Product = Product(
    id=UUID("7d3184ae-80cd-417f-8b14-e3de42a98031"),
    unit_id=SAMPLE_UNIT.id,
    name="Test Product",
    barcode="123456789",
    price=Decimal("520"),
)


class TestProductEndpoints:
    @pytest.fixture
    def mock_product_service(self) -> Mock:
        return Mock(spec=ProductService)

    @pytest.fixture
    def client_with_mocked_service(
        self, mock_product_service: Mock
    ) -> Generator[TestClient, None, None]:
        app.dependency_overrides[get_product_service] = lambda: mock_product_service
        yield TestClient(app)
        app.dependency_overrides = {}

    def test_create_product_success(
        self, client_with_mocked_service: TestClient, mock_product_service: Mock
    ) -> None:
        mock_product_service.create_product.return_value = SAMPLE_PRODUCT
        product_data: dict[str, str] = {
            "unit_id": str(SAMPLE_UNIT.id),
            "name": "Test Product",
            "barcode": "123456789",
            "price": "520",
        }

        response = client_with_mocked_service.post("/products", json=product_data)

        assert response.status_code == 201
        assert response.json()["product"]["name"] == "Test Product"

    def test_create_product_duplicate(
        self, client_with_mocked_service: TestClient, mock_product_service: Mock
    ) -> None:
        mock_product_service.create_product.side_effect = DuplicateError(
            "Duplicate barcode"
        )
        product_data: dict[str, str] = {
            "unit_id": str(SAMPLE_UNIT.id),
            "name": "Test Product",
            "barcode": "123456789",
            "price": "520",
        }

        response = client_with_mocked_service.post("/products", json=product_data)

        assert response.status_code == 409
        assert "Duplicate barcode" in response.json()["detail"]["message"]

    def test_get_product_success(
        self, client_with_mocked_service: TestClient, mock_product_service: Mock
    ) -> None:
        mock_product_service.get_product_by_id.return_value = SAMPLE_PRODUCT

        response = client_with_mocked_service.get(f"/products/{SAMPLE_PRODUCT.id}")

        assert response.status_code == 200
        assert response.json()["product"]["id"] == str(SAMPLE_PRODUCT.id)

    def test_get_product_not_found(
        self, client_with_mocked_service: TestClient, mock_product_service: Mock
    ) -> None:
        mock_product_service.get_product_by_id.side_effect = NotFoundError(
            "Product not found"
        )

        response = client_with_mocked_service.get(f"/products/{SAMPLE_PRODUCT.id}")

        assert response.status_code == 404

    def test_list_products(
        self, client_with_mocked_service: TestClient, mock_product_service: Mock
    ) -> None:
        mock_product_service.get_all_products.return_value = [SAMPLE_PRODUCT]

        response = client_with_mocked_service.get("/products")

        assert response.status_code == 200
        assert len(response.json()["products"]) == 1

    def test_update_product_success(
        self, client_with_mocked_service: TestClient, mock_product_service: Mock
    ) -> None:
        updated_product: Product = Product(
            id=SAMPLE_PRODUCT.id,
            unit_id=SAMPLE_PRODUCT.unit_id,
            name="Updated Product",
            barcode=SAMPLE_PRODUCT.barcode,
            price=SAMPLE_PRODUCT.price,
        )
        mock_product_service.update_product.return_value = updated_product
        update_data: dict[str, str] = {"name": "Updated Product"}

        response = client_with_mocked_service.patch(
            f"/products/{SAMPLE_PRODUCT.id}", json=update_data
        )

        assert response.status_code == 200
        assert response.json()["product"]["name"] == "Updated Product"
