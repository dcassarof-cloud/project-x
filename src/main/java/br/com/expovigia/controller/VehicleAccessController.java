package br.com.expovigia.controller;

import br.com.expovigia.dto.VehicleAccessResponse;
import br.com.expovigia.dto.VehicleEntryRequest;
import br.com.expovigia.dto.VehicleExitRequest;
import br.com.expovigia.service.VehicleAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/access")
@RequiredArgsConstructor
public class VehicleAccessController {

    private final VehicleAccessService vehicleAccessService;

    @PostMapping("/entry")
    public ResponseEntity<VehicleAccessResponse> registerEntry(@Valid @RequestBody VehicleEntryRequest request) {
        VehicleAccessResponse response = vehicleAccessService.registerEntry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/exit")
    public ResponseEntity<VehicleAccessResponse> registerExit(@Valid @RequestBody VehicleExitRequest request) {
        VehicleAccessResponse response = vehicleAccessService.registerExit(request);
        return ResponseEntity.ok(response);
    }
}
