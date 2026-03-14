package br.com.expovigia.controller;

import br.com.expovigia.dto.CameraReadRequest;
import br.com.expovigia.dto.VehicleAccessResponse;
import br.com.expovigia.service.CameraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/camera")
@RequiredArgsConstructor
public class CameraController {

    private final CameraService cameraService;

    @PostMapping("/read")
    public ResponseEntity<VehicleAccessResponse> read(@Valid @RequestBody CameraReadRequest request) {
        VehicleAccessResponse response = cameraService.read(request);
        return ResponseEntity.ok(response);
    }
}
