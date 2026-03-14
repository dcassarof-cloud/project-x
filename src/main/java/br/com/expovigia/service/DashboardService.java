package br.com.expovigia.service;

import br.com.expovigia.dto.DashboardSummaryResponse;
import br.com.expovigia.repository.AlertLogRepository;
import br.com.expovigia.repository.PersonFlowRepository;
import br.com.expovigia.repository.VehicleAccessRepository;
import br.com.expovigia.enums.VehicleAccessStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VehicleAccessRepository vehicleAccessRepository;
    private final PersonFlowRepository personFlowRepository;
    private final AlertLogRepository alertLogRepository;

    public DashboardSummaryResponse getSummary() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        long vehiclesInPark = vehicleAccessRepository.countByStatus(VehicleAccessStatus.INSIDE);
        long vehicleEntriesToday = vehicleAccessRepository.countEntriesBetween(start, end);
        long vehicleExitsToday = vehicleAccessRepository.countExitsBetween(start, end);

        PersonFlowRepository.PersonFlowTotalsProjection todayPeopleTotals = personFlowRepository.findTotalsBetween(start, end);
        PersonFlowRepository.PersonFlowTotalsProjection peopleTotals = personFlowRepository.findTotals();

        long peopleEntriesToday = getSafeTotal(todayPeopleTotals.getEntries());
        long peopleExitsToday = getSafeTotal(todayPeopleTotals.getExits());
        long peopleInPark = getSafeTotal(peopleTotals.getEntries()) - getSafeTotal(peopleTotals.getExits());

        long activeAlerts = alertLogRepository.countBySentAtBetween(start, end);

        return DashboardSummaryResponse.builder()
                .vehiclesInPark(vehiclesInPark)
                .peopleInPark(peopleInPark)
                .vehicleEntriesToday(vehicleEntriesToday)
                .vehicleExitsToday(vehicleExitsToday)
                .peopleEntriesToday(peopleEntriesToday)
                .peopleExitsToday(peopleExitsToday)
                .activeAlerts(activeAlerts)
                .build();
    }

    private long getSafeTotal(Long value) {
        return value == null ? 0L : value;
    }
}
