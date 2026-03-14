package br.com.expovigia.service;

import br.com.expovigia.dto.CameraAnalyzeResponse;
import br.com.expovigia.dto.OcrResult;
import br.com.expovigia.entity.Vehicle;
import br.com.expovigia.entity.VehicleAccess;
import br.com.expovigia.enums.CameraDirection;
import br.com.expovigia.enums.VehicleAccessStatus;
import br.com.expovigia.repository.VehicleAccessRepository;
import br.com.expovigia.repository.VehicleRepository;
import br.com.expovigia.util.PlateUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CameraRecognitionService {

    private final PlateOcrService plateOcrService;
    private final VehicleRepository vehicleRepository;
    private final VehicleAccessRepository vehicleAccessRepository;

    public CameraAnalyzeResponse analyze(MultipartFile file, String gate, CameraDirection direction) {
        OcrResult ocrResult = plateOcrService.extractPlateText(file);

        String filteredPlate = filterRawPlate(ocrResult.detectedText());
        String normalizedPlate = PlateUtils.normalize(filteredPlate);

        if (normalizedPlate == null || normalizedPlate.isBlank()) {
            return CameraAnalyzeResponse.builder()
                    .detectedPlate(ocrResult.detectedText())
                    .normalizedPlate(normalizedPlate)
                    .confidence(ocrResult.confidence())
                    .found(false)
                    .gate(gate)
                    .direction(direction.name())
                    .message("Não foi possível detectar uma placa válida")
                    .build();
        }

        Optional<Vehicle> vehicleOpt = vehicleRepository.findByPlate(normalizedPlate);

        if (vehicleOpt.isEmpty()) {
            return CameraAnalyzeResponse.builder()
                    .detectedPlate(filteredPlate)
                    .normalizedPlate(normalizedPlate)
                    .confidence(ocrResult.confidence())
                    .found(false)
                    .gate(gate)
                    .direction(direction.name())
                    .message("Placa não cadastrada")
                    .build();
        }

        Vehicle vehicle = vehicleOpt.get();
        Optional<VehicleAccess> openAccess = vehicleAccessRepository
                .findFirstByPlateAndStatusOrderByEntryTimeDesc(normalizedPlate, VehicleAccessStatus.INSIDE);

        return CameraAnalyzeResponse.builder()
                .detectedPlate(filteredPlate)
                .normalizedPlate(normalizedPlate)
                .confidence(ocrResult.confidence())
                .found(true)
                .company(vehicle.getCompanyName())
                .responsible(vehicle.getResponsibleName())
                .phone(vehicle.getPhone())
                .status(openAccess.map(VehicleAccess::getStatus).orElse(null))
                .entryTime(openAccess.map(VehicleAccess::getEntryTime).orElse(null))
                .gate(gate)
                .direction(direction.name())
                .message("Veículo encontrado")
                .build();
    }

    private String filterRawPlate(String rawText) {
        if (rawText == null) {
            return null;
        }

        return rawText.replaceAll("[^A-Za-z0-9]", "");
    }
}
