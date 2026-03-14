package br.com.expovigia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateExhibitorRequest {

    @NotBlank
    @Pattern(regexp = "\\d{14}", message = "must contain exactly 14 digits")
    private String cnpj;

    @NotBlank
    @Size(max = 255)
    private String corporateName;

    @NotBlank
    @Size(max = 255)
    private String responsibleName;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(max = 100)
    private String area;
}
