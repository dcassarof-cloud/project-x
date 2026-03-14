from app.schemas import PlateCandidate
from app.services.plate_normalizer import canonical_plate, generate_ambiguous_variants, normalize_plate


def consolidate_candidates(
    raw_candidates: list[PlateCandidate],
    min_confidence: float,
    max_candidates: int,
) -> list[PlateCandidate]:
    dedup: dict[str, PlateCandidate] = {}

    for candidate in raw_candidates:
        normalized = normalize_plate(candidate.normalized_text or candidate.text)
        if not normalized:
            continue
        if candidate.confidence < min_confidence:
            continue

        candidate = PlateCandidate(text=candidate.text, normalizedText=normalized, confidence=round(candidate.confidence, 4))
        key = canonical_plate(normalized) or normalized
        previous = dedup.get(key)
        if not previous or candidate.confidence > previous.confidence:
            dedup[key] = candidate

        for variant in generate_ambiguous_variants(normalized, limit=4):
            variant_key = canonical_plate(variant) or variant
            variant_candidate = PlateCandidate(
                text=variant,
                normalizedText=variant,
                confidence=round(max(candidate.confidence - 0.08, 0.0), 4),
            )
            previous_variant = dedup.get(variant_key)
            if not previous_variant or variant_candidate.confidence > previous_variant.confidence:
                dedup[variant_key] = variant_candidate

    return sorted(dedup.values(), key=lambda item: item.confidence, reverse=True)[:max_candidates]
