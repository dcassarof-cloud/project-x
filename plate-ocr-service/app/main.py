from fastapi import FastAPI

from app.api import router

app = FastAPI(title="plate-ocr-service", version="1.0.0")
app.include_router(router)
