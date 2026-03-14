package br.com.expovigia.dto;

import lombok.Builder;

@Builder
public record PeopleCurrentResponse(
        long currentPeople
) {
}
