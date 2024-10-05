from fastapi import FastAPI

from app.api.routes import products, receipts, sales, units

app = FastAPI()

app.include_router(units.router)
app.include_router(products.router)
app.include_router(receipts.router)
app.include_router(sales.router)
