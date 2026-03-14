import re

AMBIGUOUS_MAP = {
    "O": "0",
    "I": "1",
    "Z": "2",
    "S": "5",
    "B": "8",
}


def normalize_plate(text: str | None) -> str | None:
    if not text:
        return None

    cleaned = re.sub(r"[^A-Za-z0-9]", "", text.upper())
    return cleaned or None


def canonical_plate(text: str | None) -> str | None:
    normalized = normalize_plate(text)
    if not normalized:
        return None
    return "".join(AMBIGUOUS_MAP.get(ch, ch) for ch in normalized)
