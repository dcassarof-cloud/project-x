package br.com.expovigia.dto;

import br.com.expovigia.enums.VehicleAccessStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VehicleAccessResponse(
        Long id,
        String plate,
        String gate,
        LocalDateTime entryTime,
        LocalDateTime exitTime,
        VehicleAccessStatus status
) {
}
