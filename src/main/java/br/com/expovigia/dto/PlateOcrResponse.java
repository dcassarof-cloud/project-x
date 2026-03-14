package br.com.expovigia.dto;

import java.util.List;

public record PlateOcrResponse(
        String rawText,
        String normalizedPlate,
        double confidence,
        boolean plateRegionDetected,
        List<PlateCandidateResponse> candidates
) {

    public static PlateOcrResponse empty() {
        return new PlateOcrResponse(null, null, 0.0D, false, List.of());
    }
}
