package br.com.expovigia.dto;

import br.com.expovigia.enums.VehicleAccessStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record PlateAnalysisResponse(
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
        boolean requiresConfirmation,
        boolean requiresManualInput,
        List<PlateCandidateResponse> candidates,
        String message
) {
}
