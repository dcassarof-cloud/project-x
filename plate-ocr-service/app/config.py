from functools import lru_cache

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_prefix="PLATE_OCR_", case_sensitive=False)

    host: str = Field(default="0.0.0.0")
    port: int = Field(default=8001, ge=1, le=65535)
    log_level: str = Field(default="INFO")

    ocr_engine: str = Field(default="tesseract")
    paddle_language: str = Field(default="en")

    min_candidate_confidence: float = Field(default=0.20, ge=0.0, le=1.0)
    max_candidates: int = Field(default=5, ge=1, le=20)
    max_regions: int = Field(default=4, ge=1, le=10)

    adaptive_threshold_block_size: int = Field(default=19, ge=3)
    adaptive_threshold_c: int = Field(default=3, ge=0)


@lru_cache(maxsize=1)
def get_settings() -> Settings:
    return Settings()
