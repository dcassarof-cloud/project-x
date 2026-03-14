from app.schemas import PlateCandidate
from app.services.plate_normalizer import canonical_plate


def consolidate_candidates(raw_candidates: list[PlateCandidate]) -> list[PlateCandidate]:
    dedup: dict[str, PlateCandidate] = {}

    for candidate in raw_candidates:
        key = canonical_plate(candidate.normalized_text) or candidate.normalized_text
        previous = dedup.get(key)
        if not previous or candidate.confidence > previous.confidence:
            dedup[key] = candidate

    return sorted(dedup.values(), key=lambda item: item.confidence, reverse=True)
