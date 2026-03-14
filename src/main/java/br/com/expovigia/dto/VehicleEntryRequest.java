package br.com.expovigia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleEntryRequest {

    @NotBlank
    @Size(max = 10)
    private String plate;

    @NotBlank
    @Size(max = 50)
    private String gate;
}
