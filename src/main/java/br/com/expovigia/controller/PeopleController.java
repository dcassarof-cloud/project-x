package br.com.expovigia.controller;

import br.com.expovigia.dto.PeopleCurrentResponse;
import br.com.expovigia.dto.PeopleStatsResponse;
import br.com.expovigia.dto.PersonEntryRequest;
import br.com.expovigia.dto.PersonExitRequest;
import br.com.expovigia.dto.PersonFlowResponse;
import br.com.expovigia.enums.PersonFlowType;
import br.com.expovigia.service.PersonFlowService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/people")
@RequiredArgsConstructor
public class PeopleController {

    private final PersonFlowService personFlowService;

    @PostMapping("/entry")
    public ResponseEntity<PersonFlowResponse> registerEntry(@Valid @RequestBody PersonEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personFlowService.registerEntry(request));
    }

    @PostMapping("/exit")
    public ResponseEntity<PersonFlowResponse> registerExit(@Valid @RequestBody PersonExitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personFlowService.registerExit(request));
    }

    @GetMapping("/current")
    public ResponseEntity<PeopleCurrentResponse> getCurrentPeople() {
        return ResponseEntity.ok(personFlowService.getCurrentPeople());
    }

    @GetMapping("/stats")
    public ResponseEntity<PeopleStatsResponse> getStats() {
        return ResponseEntity.ok(personFlowService.getTodayStats());
    }

    @GetMapping("/history")
    public ResponseEntity<List<PersonFlowResponse>> getHistory(
            @RequestParam(required = false) PersonFlowType type,
            @RequestParam(required = false) String gate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "50") int limit
    ) {
        return ResponseEntity.ok(personFlowService.getHistory(type, gate, from, to, limit));
    }
}
