package br.com.expovigia.controller;

import br.com.expovigia.dto.CreateExhibitorRequest;
import br.com.expovigia.dto.ExhibitorResponse;
import br.com.expovigia.service.ExhibitorService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exhibitors")
@RequiredArgsConstructor
public class ExhibitorController {

    private final ExhibitorService exhibitorService;

    @PostMapping
    public ResponseEntity<ExhibitorResponse> create(@Valid @RequestBody CreateExhibitorRequest request) {
        ExhibitorResponse response = exhibitorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExhibitorResponse>> findAll() {
        return ResponseEntity.ok(exhibitorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExhibitorResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(exhibitorService.findById(id));
    }
}
