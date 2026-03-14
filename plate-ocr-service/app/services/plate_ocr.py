import logging
from dataclasses import dataclass

import cv2
import numpy as np
import pytesseract

from app.schemas import PlateCandidate
from app.services.plate_normalizer import normalize_plate

logger = logging.getLogger(__name__)

TESSERACT_CONFIG = "--oem 3 --psm 7 -c tessedit_char_whitelist=ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"


@dataclass
class OcrEngineConfig:
    engine: str
    paddle_language: str


class PlateOcrEngine:
    def __init__(self, config: OcrEngineConfig):
        self.config = config
        self._paddle = None

        if config.engine.lower() == "paddle":
            try:
                from paddleocr import PaddleOCR  # type: ignore

                self._paddle = PaddleOCR(use_angle_cls=True, lang=config.paddle_language, show_log=False)
                logger.info("OCR engine inicializada com PaddleOCR")
            except Exception as ex:  # noqa: BLE001
                logger.warning("Falha ao iniciar PaddleOCR, fallback para Tesseract: %s", ex)
                self.config.engine = "tesseract"

    def run(self, image: np.ndarray) -> list[PlateCandidate]:
        if self.config.engine.lower() == "paddle" and self._paddle is not None:
            candidates = self._run_paddle(image)
            if candidates:
                return sorted(candidates, key=lambda item: item.confidence, reverse=True)

        return self._run_tesseract(image)

    def _run_tesseract(self, image: np.ndarray) -> list[PlateCandidate]:
        data = pytesseract.image_to_data(image, output_type=pytesseract.Output.DICT, config=TESSERACT_CONFIG)
        candidates: list[PlateCandidate] = []

        for text, conf in zip(data.get("text", []), data.get("conf", [])):
            normalized = normalize_plate(text)
            if not normalized:
                continue

            try:
                confidence = max(float(conf), 0.0) / 100.0
            except (TypeError, ValueError):
                confidence = 0.0

            candidates.append(PlateCandidate(text=normalized, normalizedText=normalized, confidence=confidence))

        if not candidates:
            fallback = pytesseract.image_to_string(image, config=TESSERACT_CONFIG)
            normalized = normalize_plate(fallback)
            if normalized:
                candidates.append(PlateCandidate(text=normalized, normalizedText=normalized, confidence=0.50))

        return sorted(candidates, key=lambda item: item.confidence, reverse=True)

    def _run_paddle(self, image: np.ndarray) -> list[PlateCandidate]:
        rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB) if len(image.shape) == 3 else image
        result = self._paddle.ocr(rgb, cls=True)  # type: ignore[union-attr]

        candidates: list[PlateCandidate] = []
        for line_group in result or []:
            for line in line_group or []:
                text = line[1][0] if len(line) > 1 and line[1] else None
                conf = float(line[1][1]) if len(line) > 1 and line[1] else 0.0
                normalized = normalize_plate(text)
                if not normalized:
                    continue
                candidates.append(PlateCandidate(text=normalized, normalizedText=normalized, confidence=conf))

        return candidates
