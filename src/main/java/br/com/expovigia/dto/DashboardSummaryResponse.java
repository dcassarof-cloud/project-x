package br.com.expovigia.dto;

import lombok.Builder;

@Builder
public record DashboardSummaryResponse(
        long vehiclesInPark,
        long peopleInPark,
        long vehicleEntriesToday,
        long vehicleExitsToday,
        long peopleEntriesToday,
        long peopleExitsToday,
        long activeAlerts
) {
}
