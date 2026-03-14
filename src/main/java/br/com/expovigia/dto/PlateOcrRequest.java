package br.com.expovigia.dto;

public record PlateOcrRequest(
        byte[] imageBytes,
        String fileName,
        String contentType
) {
}
