from fastapi import APIRouter, Depends, HTTPException, status

from app.core.dependencies import get_sales_service
from app.schemas import SalesResponse
from app.services.sales_service import SalesService

router = APIRouter()


@router.get("/sales", response_model=SalesResponse)
async def get_sales_report(
    sales_service: SalesService = Depends(get_sales_service),
) -> SalesResponse:
    try:
        sales = sales_service.get_sales_report()
        return SalesResponse(sales=sales)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"message": str(e)},
        )
