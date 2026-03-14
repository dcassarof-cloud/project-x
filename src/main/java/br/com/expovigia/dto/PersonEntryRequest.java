package br.com.expovigia.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PersonEntryRequest(
        @NotNull
        @Min(1)
        Integer quantity,

        @NotBlank
        @Size(max = 50)
        String gate,

        @NotBlank
        @Size(max = 100)
        String source
) {
}
