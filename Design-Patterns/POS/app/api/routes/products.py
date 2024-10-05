from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException, status

from app.core.dependencies import get_product_service
from app.domain.exceptions import DuplicateError, NotFoundError
from app.schemas import (
    ProductCreate,
    ProductResponse,
    ProductsListResponse,
    ProductUpdate,
)
from app.services.product_service import ProductService

router = APIRouter()


@router.post(
    "/products", response_model=ProductResponse, status_code=status.HTTP_201_CREATED
)
async def create_product(
    product_create: ProductCreate,
    product_service: ProductService = Depends(get_product_service),
) -> ProductResponse:
    try:
        product = product_service.create_product(
            unit_id=product_create.unit_id,
            name=product_create.name,
            barcode=product_create.barcode,
            price=product_create.price,
        )
        return ProductResponse(product=product)
    except DuplicateError as e:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT, detail={"message": str(e)}
        )
    except NotFoundError as e:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail={"message": str(e)}
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"message": str(e)},
        )


@router.get("/products/{product_id}", response_model=ProductResponse)
async def get_product(
    product_id: UUID, product_service: ProductService = Depends(get_product_service)
) -> ProductResponse:
    try:
        product = product_service.get_product_by_id(product_id)
        return ProductResponse(product=product)
    except NotFoundError as e:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail={"message": str(e)}
        )
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"message": "Internal server error"},
        )


@router.get("/products", response_model=ProductsListResponse)
async def list_products(
    product_service: ProductService = Depends(get_product_service),
) -> ProductsListResponse:
    try:
        products = product_service.get_all_products()
        return ProductsListResponse(products=products)
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"message": "Internal server error"},
        )


@router.patch("/products/{product_id}", response_model=ProductResponse)
async def update_product(
    product_id: UUID,
    product_update: ProductUpdate,
    product_service: ProductService = Depends(get_product_service),
) -> ProductResponse:
    try:
        updates = {
            k: v for k, v in product_update.model_dump().items() if v is not None
        }
        if not updates:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail={"message": "No valid update fields provided"},
            )

        product = product_service.update_product(product_id, updates)
        return ProductResponse(product=product)
    except NotFoundError as e:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail={"message": str(e)}
        )
    except DuplicateError as e:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT, detail={"message": str(e)}
        )
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"message": "Internal server error"},
        )
