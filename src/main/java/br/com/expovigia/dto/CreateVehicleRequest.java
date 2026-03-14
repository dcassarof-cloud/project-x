package br.com.expovigia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateVehicleRequest(
        @NotBlank
        @Size(max = 10)
        String plate,

        @Size(max = 200)
        String companyName,

        @Size(max = 200)
        String responsibleName,

        @Size(max = 30)
        String phone,

        @Size(max = 50)
        String gate,

        @Size(max = 50)
        String status
) {
}
