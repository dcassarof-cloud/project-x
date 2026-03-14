package br.com.expovigia.dto;

import lombok.Builder;

@Builder
public record VehicleResponse(
        Long id,
        String plate,
        Long exhibitorId,
        String exhibitorCorporateName
) {
}
