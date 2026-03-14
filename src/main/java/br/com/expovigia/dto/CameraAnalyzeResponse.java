package br.com.expovigia.dto;

import br.com.expovigia.enums.VehicleAccessStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CameraAnalyzeResponse(
        String detectedPlate,
        String normalizedPlate,
        Double confidence,
        boolean found,
        String company,
        String responsible,
        String phone,
        VehicleAccessStatus status,
        LocalDateTime entryTime,
        String gate,
        String direction,
        String message
) {
}
