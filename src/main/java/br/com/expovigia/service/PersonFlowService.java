package br.com.expovigia.service;

import br.com.expovigia.dto.PeopleCurrentResponse;
import br.com.expovigia.dto.PeopleStatsResponse;
import br.com.expovigia.dto.PersonEntryRequest;
import br.com.expovigia.dto.PersonExitRequest;
import br.com.expovigia.dto.PersonFlowResponse;
import br.com.expovigia.entity.PersonFlow;
import br.com.expovigia.enums.PersonFlowType;
import br.com.expovigia.repository.PersonFlowRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonFlowService {

    private final PersonFlowRepository personFlowRepository;

    public PersonFlowResponse registerEntry(PersonEntryRequest request) {
        PersonFlow personFlow = buildPersonFlow(PersonFlowType.ENTRY, request.quantity(), request.gate(), request.source());
        return toResponse(personFlowRepository.save(personFlow));
    }

    public PersonFlowResponse registerExit(PersonExitRequest request) {
        PersonFlow personFlow = buildPersonFlow(PersonFlowType.EXIT, request.quantity(), request.gate(), request.source());
        return toResponse(personFlowRepository.save(personFlow));
    }

    public PeopleCurrentResponse getCurrentPeople() {
        long currentPeople = calculateCurrentPeople();
        return PeopleCurrentResponse.builder()
                .currentPeople(currentPeople)
                .build();
    }

    public PeopleStatsResponse getTodayStats() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        PersonFlowRepository.PersonFlowTotalsProjection totals = personFlowRepository.findTotalsBetween(start, end);
        long entriesToday = getSafeTotal(totals.getEntries());
        long exitsToday = getSafeTotal(totals.getExits());

        return PeopleStatsResponse.builder()
                .entriesToday(entriesToday)
                .exitsToday(exitsToday)
                .currentPeople(calculateCurrentPeople())
                .build();
    }

    public List<PersonFlowResponse> getHistory(PersonFlowType type, String gate, LocalDateTime from, LocalDateTime to, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 200);

        return personFlowRepository
                .findRecentWithFilters(
                        type,
                        gate,
                        from,
                        to,
                        PageRequest.of(0, safeLimit, Sort.by(Sort.Direction.DESC, "recordedAt"))
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private long calculateCurrentPeople() {
        PersonFlowRepository.PersonFlowTotalsProjection totals = personFlowRepository.findTotals();
        return getSafeTotal(totals.getEntries()) - getSafeTotal(totals.getExits());
    }

    private PersonFlow buildPersonFlow(PersonFlowType type, Integer quantity, String gate, String source) {
        return PersonFlow.builder()
                .type(type)
                .quantity(quantity)
                .gate(gate)
                .source(source)
                .recordedAt(LocalDateTime.now())
                .build();
    }

    private PersonFlowResponse toResponse(PersonFlow personFlow) {
        return PersonFlowResponse.builder()
                .id(personFlow.getId())
                .type(personFlow.getType())
                .quantity(personFlow.getQuantity())
                .gate(personFlow.getGate())
                .source(personFlow.getSource())
                .recordedAt(personFlow.getRecordedAt())
                .build();
    }

    private long getSafeTotal(Long value) {
        return value == null ? 0L : value;
    }
}
