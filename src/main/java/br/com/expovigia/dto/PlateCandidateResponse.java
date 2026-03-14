package br.com.expovigia.dto;

public record PlateCandidateResponse(
        String text,
        String normalizedText,
        double confidence
) {
}
