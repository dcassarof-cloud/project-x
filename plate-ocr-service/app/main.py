import logging

from fastapi import FastAPI

from app.api import router
from app.config import get_settings

settings = get_settings()
logging.basicConfig(
    level=getattr(logging, settings.log_level.upper(), logging.INFO),
    format="%(asctime)s %(levelname)s [%(name)s] %(message)s",
)

app = FastAPI(title="plate-ocr-service", version="1.0.0")
app.include_router(router)
