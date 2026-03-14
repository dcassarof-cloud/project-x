package br.com.expovigia.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PlateLookupResponse(
        boolean found,
        String plate,
        String company,
        String responsible,
        String phone,
        String gate,
        String status,
        LocalDateTime entryTime,
        String notes
) {

    public static PlateLookupResponse notFound() {
        return PlateLookupResponse.builder()
                .found(false)
                .build();
    }
}
