package br.com.expovigia.dto;

import lombok.Builder;

@Builder
public record PeopleStatsResponse(
        long entriesToday,
        long exitsToday,
        long currentPeople
) {
}
