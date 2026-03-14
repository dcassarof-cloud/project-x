import cv2
import numpy as np


def decode_image_bytes(payload: bytes) -> np.ndarray | None:
    image_array = np.frombuffer(payload, np.uint8)
    return cv2.imdecode(image_array, cv2.IMREAD_COLOR)


def preprocess_image(image: np.ndarray, block_size: int, threshold_c: int) -> np.ndarray:
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    contrast = cv2.equalizeHist(gray)
    denoised = cv2.bilateralFilter(contrast, 9, 75, 75)

    if block_size % 2 == 0:
        block_size += 1

    adaptive = cv2.adaptiveThreshold(
        denoised,
        255,
        cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
        cv2.THRESH_BINARY,
        block_size,
        threshold_c,
    )

    morph = cv2.morphologyEx(adaptive, cv2.MORPH_CLOSE, np.ones((3, 3), np.uint8))
    return morph


def detect_plate_regions(image: np.ndarray) -> list[np.ndarray]:
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    filtered = cv2.bilateralFilter(gray, 11, 17, 17)
    edges = cv2.Canny(filtered, 30, 180)

    contours, _ = cv2.findContours(edges, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

    regions: list[tuple[float, np.ndarray]] = []
    image_h, image_w = image.shape[:2]
    image_area = image_h * image_w

    for contour in contours:
        perimeter = cv2.arcLength(contour, True)
        approx = cv2.approxPolyDP(contour, 0.02 * perimeter, True)
        x, y, w, h = cv2.boundingRect(approx)
        if h == 0:
            continue

        aspect_ratio = w / float(h)
        area = w * h

        if not (2.0 <= aspect_ratio <= 6.5):
            continue
        if area < image_area * 0.01 or area > image_area * 0.5:
            continue
        if w < 80 or h < 20:
            continue

        roi = image[y : y + h, x : x + w]
        score = area * min(aspect_ratio, 6.5)
        regions.append((score, roi))

    regions.sort(key=lambda item: item[0], reverse=True)
    return [roi for _, roi in regions]
