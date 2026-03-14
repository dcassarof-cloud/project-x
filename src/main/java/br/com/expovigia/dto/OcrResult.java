package br.com.expovigia.dto;

public record OcrResult(
        String detectedText,
        double confidence
) {
}
