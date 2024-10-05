from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException, status
from starlette.responses import JSONResponse

from app.core.dependencies import get_unit_service
from app.domain.exceptions import DuplicateError, NotFoundError
from app.schemas import UnitCreate, UnitResponse, UnitsListResponse
from app.services.unit_service import UnitService

router = APIRouter()


@router.post("/units", response_model=UnitResponse, status_code=status.HTTP_201_CREATED)
async def create_unit(
    unit_create: UnitCreate, unit_service: UnitService = Depends(get_unit_service)
) -> UnitResponse:
    try:
        unit = unit_service.create_unit(unit_create.name)
        return UnitResponse(unit=unit)
    except DuplicateError as e:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT, detail={"message": str(e)}
        )
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"message": "Internal server error"},
        )


@router.get("/units/{unit_id}", response_model=UnitResponse)
async def get_unit(
    unit_id: UUID, unit_service: UnitService = Depends(get_unit_service)
) -> UnitResponse:
    try:
        unit = unit_service.get_unit_by_id(unit_id)
        return UnitResponse(unit=unit)
    except NotFoundError as e:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail={"message": str(e)}
        )
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"message": "Internal server error"},
        )


@router.get("/units", response_model=UnitsListResponse)
async def list_units(
    unit_service: UnitService = Depends(get_unit_service),
) -> UnitsListResponse:
    try:
        units = unit_service.get_all_units()
        return UnitsListResponse(units=units)
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"message": "Internal server error"},
        )


@router.put("/units/{unit_id}", response_model=UnitResponse)
async def update_unit(
    unit_id: UUID,
    unit_update: UnitCreate,
    unit_service: UnitService = Depends(get_unit_service),
) -> UnitResponse:
    try:
        unit = unit_service.update_unit(unit_id, unit_update.name)
        return UnitResponse(unit=unit)
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


@router.delete("/units/{unit_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_unit(
    unit_id: UUID, unit_service: UnitService = Depends(get_unit_service)
) -> JSONResponse:
    try:
        unit_service.delete_unit(unit_id)
        return JSONResponse(status_code=status.HTTP_200_OK, content={})
    except NotFoundError as e:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail={"message": str(e)}
        )
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail={"message": str(e)}
        )
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"message": "Internal server error"},
        )
