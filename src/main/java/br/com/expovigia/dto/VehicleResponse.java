package br.com.expovigia.dto;

import java.time.LocalDateTime;

public record VehicleResponse(
        Long id,
        String plate,
        String companyName,
        String responsibleName,
        String phone,
        String gate,
        String status,
        LocalDateTime createdAt
) {
}
