from typing import Any

from pydantic import BaseModel, ConfigDict, Field


class PlateCandidate(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    text: str
    normalized_text: str = Field(alias="normalizedText")
    confidence: float


class PlateOcrResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    raw_text: str | None = Field(default=None, alias="rawText")
    normalized_plate: str | None = Field(default=None, alias="normalizedPlate")
    confidence: float = 0.0
    plate_region_detected: bool = Field(default=False, alias="plateRegionDetected")
    candidates: list[PlateCandidate] = Field(default_factory=list)
    debug: dict[str, Any] | None = None
