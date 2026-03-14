from pydantic import BaseModel, Field


class PlateCandidate(BaseModel):
    text: str
    normalized_text: str = Field(alias="normalizedText")
    confidence: float

    class Config:
        populate_by_name = True


class PlateOcrResponse(BaseModel):
    raw_text: str | None = Field(default=None, alias="rawText")
    normalized_plate: str | None = Field(default=None, alias="normalizedPlate")
    confidence: float = 0.0
    plate_region_detected: bool = Field(default=False, alias="plateRegionDetected")
    candidates: list[PlateCandidate] = Field(default_factory=list)
    debug: dict | None = None

    class Config:
        populate_by_name = True
