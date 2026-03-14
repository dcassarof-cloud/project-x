import cv2
import numpy as np


def detect_plate_regions(image: np.ndarray) -> list[np.ndarray]:
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    filtered = cv2.bilateralFilter(gray, 11, 17, 17)
    edges = cv2.Canny(filtered, 30, 200)
    contours, _ = cv2.findContours(edges, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

    regions: list[np.ndarray] = []
    for contour in sorted(contours, key=cv2.contourArea, reverse=True)[:20]:
      x, y, w, h = cv2.boundingRect(contour)
      aspect_ratio = w / float(h)
      if 2.0 <= aspect_ratio <= 6.5 and w > 80 and h > 20:
          regions.append(image[y:y + h, x:x + w])

    return regions
