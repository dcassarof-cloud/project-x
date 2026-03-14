package br.com.expovigia.dto;

import lombok.Builder;

@Builder
public record CameraAnalyzeResponse(
        String action,
        PlateAnalysisResponse analysis,
        String message
) {
}
