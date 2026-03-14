import pytesseract
import cv2
import numpy as np

from app.schemas import PlateCandidate
from app.services.plate_normalizer import normalize_plate


TESSERACT_CONFIG = "--oem 3 --psm 7 -c tessedit_char_whitelist=ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"


def run_ocr(image: np.ndarray) -> list[PlateCandidate]:
    data = pytesseract.image_to_data(image, output_type=pytesseract.Output.DICT, config=TESSERACT_CONFIG)
    candidates: list[PlateCandidate] = []

    for text, conf in zip(data.get("text", []), data.get("conf", [])):
        cleaned = normalize_plate(text)
        if not cleaned:
            continue

        confidence = max(float(conf), 0.0) / 100.0 if str(conf).replace(".", "", 1).isdigit() else 0.0
        candidates.append(PlateCandidate(text=cleaned, normalizedText=cleaned, confidence=confidence))

    if not candidates:
        fallback = pytesseract.image_to_string(image, config=TESSERACT_CONFIG)
        cleaned = normalize_plate(fallback)
        if cleaned:
            candidates.append(PlateCandidate(text=cleaned, normalizedText=cleaned, confidence=0.5))

    return sorted(candidates, key=lambda item: item.confidence, reverse=True)
