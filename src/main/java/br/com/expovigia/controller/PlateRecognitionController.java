package br.com.expovigia.controller;

import br.com.expovigia.dto.CameraAnalyzeResponse;
import br.com.expovigia.enums.CameraDirection;
import br.com.expovigia.service.CameraRecognitionService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/plate-recognition")
@RequiredArgsConstructor
@Validated
public class PlateRecognitionController {

    private final CameraRecognitionService cameraRecognitionService;

    @PostMapping(path = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CameraAnalyzeResponse> analyze(
            @RequestParam("file") MultipartFile file,
            @RequestParam("gate") @NotBlank String gate,
            @RequestParam("direction") @NotNull CameraDirection direction
    ) {
        CameraAnalyzeResponse response = cameraRecognitionService.analyze(file, gate, direction);
        return ResponseEntity.ok(response);
    }
}
