import re
from itertools import product

AMBIGUOUS_MAP = {
    "0": ["0", "O"],
    "O": ["O", "0"],
    "1": ["1", "I"],
    "I": ["I", "1"],
    "2": ["2", "Z"],
    "Z": ["Z", "2"],
    "5": ["5", "S"],
    "S": ["S", "5"],
    "8": ["8", "B"],
    "B": ["B", "8"],
}

CANONICAL_MAP = {
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
    return "".join(CANONICAL_MAP.get(ch, ch) for ch in normalized)


def generate_ambiguous_variants(text: str | None, limit: int = 10) -> list[str]:
    normalized = normalize_plate(text)
    if not normalized:
        return []

    options_per_char = [AMBIGUOUS_MAP.get(ch, [ch]) for ch in normalized]

    variants: list[str] = []
    for combo in product(*options_per_char):
        candidate = "".join(combo)
        if candidate not in variants:
            variants.append(candidate)
        if len(variants) >= limit:
            break

    return variants
