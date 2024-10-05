from decimal import Decimal
from typing import Generator
from unittest.mock import Mock
from uuid import UUID

import pytest
from fastapi.testclient import TestClient

from app.core.dependencies import get_unit_service
from app.domain.exceptions import DuplicateError
from app.domain.models import Product, Unit
from app.services.unit_service import UnitService
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


class TestUnitEndpoints:
    @pytest.fixture
    def mock_unit_service(self) -> Mock:
        return Mock(spec=UnitService)

    @pytest.fixture
    def client_with_mocked_service(
        self, mock_unit_service: Mock
    ) -> Generator[TestClient, None, None]:
        app.dependency_overrides[get_unit_service] = lambda: mock_unit_service
        yield TestClient(app)
        app.dependency_overrides = {}

    def test_create_unit_success(
        self, client_with_mocked_service: TestClient, mock_unit_service: Mock
    ) -> None:
        mock_unit_service.create_unit.return_value = SAMPLE_UNIT
        unit_data: dict[str, str] = {"name": "Kilogram"}

        response = client_with_mocked_service.post("/units", json=unit_data)

        assert response.status_code == 201
        assert response.json()["unit"]["name"] == "Kilogram"

    def test_create_unit_duplicate(
        self, client_with_mocked_service: TestClient, mock_unit_service: Mock
    ) -> None:
        mock_unit_service.create_unit.side_effect = DuplicateError(
            "Unit already exists"
        )
        unit_data: dict[str, str] = {"name": "Kilogram"}

        response = client_with_mocked_service.post("/units", json=unit_data)

        assert response.status_code == 409

    def test_get_unit_success(
        self, client_with_mocked_service: TestClient, mock_unit_service: Mock
    ) -> None:
        mock_unit_service.get_unit_by_id.return_value = SAMPLE_UNIT

        response = client_with_mocked_service.get(f"/units/{SAMPLE_UNIT.id}")

        assert response.status_code == 200
        assert response.json()["unit"]["id"] == str(SAMPLE_UNIT.id)

    def test_list_units(
        self, client_with_mocked_service: TestClient, mock_unit_service: Mock
    ) -> None:
        mock_unit_service.get_all_units.return_value = [SAMPLE_UNIT]

        response = client_with_mocked_service.get("/units")

        assert response.status_code == 200
        assert len(response.json()["units"]) == 1

    def test_update_unit_success(
        self, client_with_mocked_service: TestClient, mock_unit_service: Mock
    ) -> None:
        updated_unit: Unit = Unit(id=SAMPLE_UNIT.id, name="Updated Unit")
        mock_unit_service.update_unit.return_value = updated_unit
        update_data: dict[str, str] = {"name": "Updated Unit"}

        response = client_with_mocked_service.put(
            f"/units/{SAMPLE_UNIT.id}", json=update_data
        )

        assert response.status_code == 200
        assert response.json()["unit"]["name"] == "Updated Unit"

    def test_delete_unit_success(
        self, client_with_mocked_service: TestClient, mock_unit_service: Mock
    ) -> None:
        response = client_with_mocked_service.delete(f"/units/{SAMPLE_UNIT.id}")

        assert response.status_code == 204
        mock_unit_service.delete_unit.assert_called_once_with(SAMPLE_UNIT.id)

    def test_delete_unit_in_use(
        self, client_with_mocked_service: TestClient, mock_unit_service: Mock
    ) -> None:
        mock_unit_service.delete_unit.side_effect = ValueError("Unit is in use")

        response = client_with_mocked_service.delete(f"/units/{SAMPLE_UNIT.id}")

        assert response.status_code == 400
        assert "Unit is in use" in response.json()["detail"]["message"]
