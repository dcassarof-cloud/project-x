package br.com.expovigia.dto;

import lombok.Builder;

@Builder
public record PlateLookupResponse(
        boolean found,
        String plate,
        String companyName,
        String responsibleName,
        String phone,
        String gate,
        String status
) {

    public static PlateLookupResponse notFound() {
        return PlateLookupResponse.builder()
                .found(false)
                .build();
    }
}
