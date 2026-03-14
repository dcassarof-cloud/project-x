import logging
import time

from fastapi import APIRouter, File, HTTPException, UploadFile

from app.config import get_settings
from app.schemas import PlateOcrResponse
from app.services.candidate_generator import consolidate_candidates
from app.services.image_preprocess import decode_image_bytes, detect_plate_regions, preprocess_image
from app.services.plate_normalizer import normalize_plate
from app.services.plate_ocr import OcrEngineConfig, PlateOcrEngine

router = APIRouter()
logger = logging.getLogger(__name__)
settings = get_settings()
ocr_engine = PlateOcrEngine(OcrEngineConfig(engine=settings.ocr_engine, paddle_language=settings.paddle_language))


@router.get("/health")
def health() -> dict:
    return {"status": "ok"}


@router.post("/ocr/plate", response_model=PlateOcrResponse)
async def ocr_plate(file: UploadFile = File(...)) -> PlateOcrResponse:
    started = time.perf_counter()
    payload = await file.read()

    if not payload:
        raise HTTPException(status_code=400, detail="Empty file")

    image = decode_image_bytes(payload)
    if image is None:
        raise HTTPException(status_code=400, detail="Invalid image")

    regions = detect_plate_regions(image)
    region_detected = len(regions) > 0
    all_candidates = []

    for region in regions[: settings.max_regions]:
        prepared_region = preprocess_image(
            region,
            block_size=settings.adaptive_threshold_block_size,
            threshold_c=settings.adaptive_threshold_c,
        )
        all_candidates.extend(ocr_engine.run(prepared_region))

    fallback_preprocessed = preprocess_image(
        image,
        block_size=settings.adaptive_threshold_block_size,
        threshold_c=settings.adaptive_threshold_c,
    )
    all_candidates.extend(ocr_engine.run(fallback_preprocessed))

    candidates = consolidate_candidates(
        all_candidates,
        min_confidence=settings.min_candidate_confidence,
        max_candidates=settings.max_candidates,
    )

    best = candidates[0] if candidates else None
    elapsed_ms = int((time.perf_counter() - started) * 1000)

    logger.info(
        "OCR concluído file=%s elapsedMs=%s regionDetected=%s candidates=%s confidence=%s",
        file.filename,
        elapsed_ms,
        region_detected,
        len(candidates),
        best.confidence if best else 0.0,
    )

    return PlateOcrResponse(
        rawText=best.text if best else None,
        normalizedPlate=normalize_plate(best.normalized_text if best else None),
        confidence=best.confidence if best else 0.0,
        plateRegionDetected=region_detected,
        candidates=candidates,
        debug={
            "engine": settings.ocr_engine,
            "candidateCount": len(candidates),
            "regionsDetected": len(regions),
            "elapsedMs": elapsed_ms,
        },
    )
