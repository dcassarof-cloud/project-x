package br.com.expovigia.controller;

import br.com.expovigia.dto.CameraAnalyzeResponse;
import br.com.expovigia.dto.CameraConfirmRequest;
import br.com.expovigia.dto.CameraReadRequest;
import br.com.expovigia.dto.VehicleAccessResponse;
import br.com.expovigia.enums.CameraDirection;
import br.com.expovigia.service.CameraRecognitionService;
import br.com.expovigia.service.CameraService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/camera")
@RequiredArgsConstructor
@Validated
public class CameraController {

    private final CameraService cameraService;
    private final CameraRecognitionService cameraRecognitionService;

    @PostMapping("/read")
    public ResponseEntity<VehicleAccessResponse> read(@Valid @RequestBody CameraReadRequest request) {
        VehicleAccessResponse response = cameraService.read(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CameraAnalyzeResponse> analyze(
            @RequestParam("file") MultipartFile file,
            @RequestParam("gate") @NotBlank String gate,
            @RequestParam("direction") @NotNull CameraDirection direction
    ) {
        CameraAnalyzeResponse response = cameraRecognitionService.analyze(file, gate, direction);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<VehicleAccessResponse> confirm(@Valid @RequestBody CameraConfirmRequest request) {
        VehicleAccessResponse response = cameraService.confirm(request);
        return ResponseEntity.ok(response);
    }
}
