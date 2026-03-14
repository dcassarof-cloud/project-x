import cv2
import numpy as np
from fastapi import APIRouter, File, HTTPException, UploadFile

from app.schemas import PlateOcrResponse
from app.services.candidate_generator import consolidate_candidates
from app.services.image_preprocess import preprocess_image
from app.services.plate_detector import detect_plate_regions
from app.services.plate_normalizer import normalize_plate
from app.services.plate_ocr import run_ocr

router = APIRouter()


@router.get("/health")
def health() -> dict:
    return {"status": "ok"}


@router.post("/ocr/plate", response_model=PlateOcrResponse)
async def ocr_plate(file: UploadFile = File(...)) -> PlateOcrResponse:
    payload = await file.read()
    if not payload:
        raise HTTPException(status_code=400, detail="Empty file")

    image_array = np.frombuffer(payload, np.uint8)
    image = cv2.imdecode(image_array, cv2.IMREAD_COLOR)
    if image is None:
        raise HTTPException(status_code=400, detail="Invalid image")

    regions = detect_plate_regions(image)
    region_detected = len(regions) > 0

    all_candidates = []
    for region in regions[:4]:
        prepared = preprocess_image(region)
        all_candidates.extend(run_ocr(prepared))

    fallback_candidates = run_ocr(preprocess_image(image))
    all_candidates.extend(fallback_candidates)

    candidates = consolidate_candidates(all_candidates)[:5]
    best = candidates[0] if candidates else None

    return PlateOcrResponse(
        rawText=best.text if best else None,
        normalizedPlate=normalize_plate(best.normalized_text if best else None),
        confidence=best.confidence if best else 0.0,
        plateRegionDetected=region_detected,
        candidates=candidates,
        debug={"candidateCount": len(candidates), "regionsDetected": len(regions)},
    )
