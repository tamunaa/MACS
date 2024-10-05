from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel
from starlette.responses import JSONResponse

from app.core.dependencies import get_receipt_service
from app.domain.exceptions import ForbiddenError, NotFoundError
from app.schemas import ReceiptProductCreate, ReceiptResponse
from app.services.receipt_service import ReceiptService

router = APIRouter()


@router.post(
    "/receipts", response_model=ReceiptResponse, status_code=status.HTTP_201_CREATED
)
async def create_receipt(
    receipt_service: ReceiptService = Depends(get_receipt_service),
) -> ReceiptResponse:
    try:
        receipt = receipt_service.create_receipt()
        return ReceiptResponse(receipt=receipt)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"message": str(e)},
        )


@router.post(
    "/receipts/{receipt_id}/products",
    response_model=ReceiptResponse,
    status_code=status.HTTP_201_CREATED,
)
async def add_product(
    receipt_id: UUID,
    product_create: ReceiptProductCreate,
    receipt_service: ReceiptService = Depends(get_receipt_service),
) -> ReceiptResponse:
    try:
        receipt = receipt_service.add_product(
            receipt_id=receipt_id,
            product_id=product_create.id,
            quantity=product_create.quantity,
        )
        return ReceiptResponse(receipt=receipt)
    except NotFoundError as e:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail={"message": str(e)}
        )
    except ForbiddenError as e:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN, detail={"message": str(e)}
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"message": str(e)},
        )


@router.get("/receipts/{receipt_id}", response_model=ReceiptResponse)
async def get_receipt(
    receipt_id: UUID, receipt_service: ReceiptService = Depends(get_receipt_service)
) -> ReceiptResponse:
    try:
        receipt = receipt_service.get_receipt_by_id(receipt_id)
        return ReceiptResponse(receipt=receipt)
    except NotFoundError as e:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail={"message": str(e)}
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"message": str(e)},
        )


class CloseReceiptRequest(BaseModel):
    status: str


@router.patch("/receipts/{receipt_id}")
async def close_receipt(
    receipt_id: UUID,
    close_request: CloseReceiptRequest,
    receipt_service: ReceiptService = Depends(get_receipt_service),
) -> JSONResponse:
    try:
        if close_request.status != "closed":
            return JSONResponse(
                status_code=status.HTTP_400_BAD_REQUEST,
                content={"detail": {"message": "Invalid status update"}},
            )
        receipt_service.close_receipt(receipt_id)
        return JSONResponse(status_code=status.HTTP_200_OK, content={})

    except NotFoundError as e:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail={"message": str(e)}
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"message": str(e)},
        )


@router.delete("/receipts/{receipt_id}")
async def delete_receipt(
    receipt_id: UUID, receipt_service: ReceiptService = Depends(get_receipt_service)
) -> JSONResponse:
    try:
        receipt_service.delete_receipt(receipt_id)
        return JSONResponse(status_code=status.HTTP_200_OK, content={})
    except NotFoundError as e:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail={"message": str(e)}
        )
    except ForbiddenError as e:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN, detail={"message": str(e)}
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"message": str(e)},
        )
