package br.com.expovigia.dto;

import lombok.Builder;

@Builder
public record ExhibitorResponse(
        Long id,
        String cnpj,
        String corporateName,
        String responsibleName,
        String phone,
        String email,
        String area
) {
}
