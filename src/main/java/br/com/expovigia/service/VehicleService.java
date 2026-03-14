package br.com.expovigia.service;

import br.com.expovigia.dto.CreateVehicleRequest;
import br.com.expovigia.dto.PlateLookupResponse;
import br.com.expovigia.dto.VehicleResponse;
import br.com.expovigia.entity.Vehicle;
import br.com.expovigia.exception.BusinessException;
import br.com.expovigia.exception.ConflictException;
import br.com.expovigia.repository.VehicleRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private static final int MIN_PLATE_LENGTH = 7;

    private final VehicleRepository vehicleRepository;

    public VehicleResponse create(CreateVehicleRequest request) {
        Vehicle vehicle = Vehicle.builder()
                .plate(request.plate())
                .companyName(request.companyName())
                .responsibleName(request.responsibleName())
                .phone(request.phone())
                .gate(request.gate())
                .status(request.status())
                .build();

        Vehicle savedVehicle = registerVehicle(vehicle);
        return toResponse(savedVehicle);
    }

    public Vehicle registerVehicle(Vehicle vehicle) {
        String normalizedPlate = normalizePlate(vehicle.getPlate());

        if (vehicleRepository.existsByPlate(normalizedPlate)) {
            throw new ConflictException("Vehicle plate already registered: " + normalizedPlate);
        }

        vehicle.setPlate(normalizedPlate);
        return vehicleRepository.save(vehicle);
    }

    public Optional<Vehicle> findByPlate(String plate) {
        return vehicleRepository.findByPlate(normalizePlate(plate));
    }

    public List<VehicleResponse> findAll() {
        return vehicleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PlateLookupResponse findByPlateResponse(String plate) {
        return findByPlate(plate)
                .map(this::toPlateLookupResponse)
                .orElseGet(PlateLookupResponse::notFound);
    }

    private String normalizePlate(String plate) {
        if (plate == null) {
            throw new BusinessException("Plate must be informed");
        }

        String normalizedPlate = plate.replaceAll("\\s+", "").toUpperCase();

        if (normalizedPlate.length() < MIN_PLATE_LENGTH) {
            throw new BusinessException("Plate must have at least " + MIN_PLATE_LENGTH + " characters");
        }

        return normalizedPlate;
    }

    private PlateLookupResponse toPlateLookupResponse(Vehicle vehicle) {
        return PlateLookupResponse.builder()
                .found(true)
                .plate(vehicle.getPlate())
                .companyName(vehicle.getCompanyName())
                .responsibleName(vehicle.getResponsibleName())
                .phone(vehicle.getPhone())
                .gate(vehicle.getGate())
                .status(vehicle.getStatus())
                .build();
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlate(),
                vehicle.getCompanyName(),
                vehicle.getResponsibleName(),
                vehicle.getPhone(),
                vehicle.getGate(),
                vehicle.getStatus(),
                vehicle.getCreatedAt()
        );
    }
}
