package br.com.expovigia.service;

import br.com.expovigia.dto.CameraAnalyzeResponse;
import br.com.expovigia.dto.PlateAnalysisResponse;
import br.com.expovigia.dto.PlateCandidateResponse;
import br.com.expovigia.dto.PlateOcrRequest;
import br.com.expovigia.dto.PlateOcrResponse;
import br.com.expovigia.entity.Vehicle;
import br.com.expovigia.entity.VehicleAccess;
import br.com.expovigia.enums.CameraDirection;
import br.com.expovigia.enums.VehicleAccessStatus;
import br.com.expovigia.repository.VehicleAccessRepository;
import br.com.expovigia.util.PlateUtils;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class CameraRecognitionService {

    private static final double HIGH_CONFIDENCE = 0.90D;
    private static final double MID_CONFIDENCE = 0.70D;

    private final PlateOcrService plateOcrService;
    private final PlateMatchingService plateMatchingService;
    private final VehicleAccessRepository vehicleAccessRepository;

    public CameraAnalyzeResponse analyze(MultipartFile file, String gate, CameraDirection direction) {
        PlateOcrResponse ocrResponse = callOcr(file);
        String normalizedPlate = PlateUtils.normalize(ocrResponse.normalizedPlate());

        Optional<Vehicle> matchedVehicle = plateMatchingService.findBestMatch(normalizedPlate, ocrResponse.candidates());
        Optional<VehicleAccess> openAccess = matchedVehicle
                .flatMap(vehicle -> vehicleAccessRepository.findFirstByPlateAndStatusOrderByEntryTimeDesc(
                        vehicle.getPlate(), VehicleAccessStatus.INSIDE));

        PlateAnalysisResponse analysis = buildAnalysis(
                ocrResponse,
                normalizedPlate,
                gate,
                direction,
                matchedVehicle,
                openAccess
        );

        return CameraAnalyzeResponse.builder()
                .action("analyzeImage")
                .analysis(analysis)
                .message(analysis.message())
                .build();
    }

    private PlateOcrResponse callOcr(MultipartFile file) {
        try {
            PlateOcrRequest request = new PlateOcrRequest(file.getBytes(), file.getOriginalFilename(), file.getContentType());
            return plateOcrService.extractPlate(request);
        } catch (IOException ex) {
            log.warn("Falha ao ler imagem para OCR: {}", ex.getMessage());
            return PlateOcrResponse.empty();
        }
    }

    private PlateAnalysisResponse buildAnalysis(
            PlateOcrResponse ocr,
            String normalizedPlate,
            String gate,
            CameraDirection direction,
            Optional<Vehicle> matchedVehicle,
            Optional<VehicleAccess> openAccess
    ) {
        double confidence = ocr.confidence();
        boolean hasPlate = normalizedPlate != null && !normalizedPlate.isBlank();

        if (!hasPlate) {
            return baseBuilder(ocr, normalizedPlate, gate, direction)
                    .found(false)
                    .requiresConfirmation(false)
                    .requiresManualInput(true)
                    .message(ocr.rawText() == null
                            ? "Serviço de OCR indisponível no momento. Informe a placa manualmente."
                            : "Não foi possível reconhecer automaticamente a placa. Informe manualmente para validar.")
                    .build();
        }

        if (confidence >= HIGH_CONFIDENCE && matchedVehicle.isPresent()) {
            Vehicle vehicle = matchedVehicle.get();
            return baseBuilder(ocr, normalizedPlate, gate, direction)
                    .found(true)
                    .company(vehicle.getCompanyName())
                    .responsible(vehicle.getResponsibleName())
                    .phone(vehicle.getPhone())
                    .status(openAccess.map(VehicleAccess::getStatus).orElse(null))
                    .entryTime(openAccess.map(VehicleAccess::getEntryTime).orElse(null))
                    .requiresConfirmation(false)
                    .requiresManualInput(false)
                    .message("Placa identificada com sucesso.")
                    .build();
        }

        if (confidence >= MID_CONFIDENCE) {
            return baseBuilder(ocr, normalizedPlate, gate, direction)
                    .found(matchedVehicle.isPresent())
                    .company(matchedVehicle.map(Vehicle::getCompanyName).orElse(null))
                    .responsible(matchedVehicle.map(Vehicle::getResponsibleName).orElse(null))
                    .phone(matchedVehicle.map(Vehicle::getPhone).orElse(null))
                    .status(openAccess.map(VehicleAccess::getStatus).orElse(null))
                    .entryTime(openAccess.map(VehicleAccess::getEntryTime).orElse(null))
                    .requiresConfirmation(true)
                    .requiresManualInput(false)
                    .message("Placa reconhecida com confiança intermediária. Confirme para continuar.")
                    .build();
        }

        return baseBuilder(ocr, normalizedPlate, gate, direction)
                .found(false)
                .requiresConfirmation(false)
                .requiresManualInput(true)
                .message("Não foi possível reconhecer automaticamente a placa. Informe manualmente para validar.")
                .build();
    }

    private PlateAnalysisResponse.PlateAnalysisResponseBuilder baseBuilder(
            PlateOcrResponse ocr,
            String normalizedPlate,
            String gate,
            CameraDirection direction
    ) {
        List<PlateCandidateResponse> candidates = ocr.candidates() == null ? List.of() : ocr.candidates();

        return PlateAnalysisResponse.builder()
                .detectedPlate(ocr.rawText())
                .normalizedPlate(normalizedPlate)
                .confidence(ocr.confidence())
                .gate(gate)
                .direction(direction.name())
                .candidates(candidates);
    }
}
