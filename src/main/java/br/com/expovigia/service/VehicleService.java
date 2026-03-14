package br.com.expovigia.service;

import br.com.expovigia.dto.CreateVehicleRequest;
import br.com.expovigia.dto.PlateLookupResponse;
import br.com.expovigia.dto.VehicleResponse;
import br.com.expovigia.entity.Exhibitor;
import br.com.expovigia.entity.Vehicle;
import br.com.expovigia.entity.VehicleAccess;
import br.com.expovigia.enums.VehicleAccessStatus;
import br.com.expovigia.exception.ConflictException;
import br.com.expovigia.exception.ResourceNotFoundException;
import br.com.expovigia.repository.ExhibitorRepository;
import br.com.expovigia.repository.VehicleAccessRepository;
import br.com.expovigia.repository.VehicleRepository;
import br.com.expovigia.util.PlateUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ExhibitorRepository exhibitorRepository;
    private final VehicleAccessRepository vehicleAccessRepository;

    public VehicleResponse create(CreateVehicleRequest request) {
        String normalizedPlate = PlateUtils.normalize(request.getPlate());

        if (vehicleRepository.existsByPlate(normalizedPlate)) {
            throw new ConflictException("Vehicle plate already registered: " + normalizedPlate);
        }

        Exhibitor exhibitor = exhibitorRepository.findById(request.getExhibitorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exhibitor not found with id: " + request.getExhibitorId()
                ));

        Vehicle vehicle = Vehicle.builder()
                .plate(normalizedPlate)
                .exhibitor(exhibitor)
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return toResponse(savedVehicle);
    }

    public List<VehicleResponse> findAll() {
        return vehicleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PlateLookupResponse findByPlate(String plate) {
        String normalizedPlate = PlateUtils.normalize(plate);

        return vehicleRepository.findByPlate(normalizedPlate)
                .map(this::toPlateLookupResponse)
                .orElseGet(PlateLookupResponse::notFound);
    }

    private PlateLookupResponse toPlateLookupResponse(Vehicle vehicle) {
        VehicleAccess openAccess = vehicleAccessRepository
                .findFirstByPlateAndStatusOrderByEntryTimeDesc(vehicle.getPlate(), VehicleAccessStatus.INSIDE)
                .orElse(null);

        return PlateLookupResponse.builder()
                .found(true)
                .plate(vehicle.getPlate())
                .company(vehicle.getExhibitor().getCorporateName())
                .responsible(vehicle.getExhibitor().getResponsibleName())
                .phone(vehicle.getExhibitor().getPhone())
                .gate(openAccess != null ? openAccess.getGate() : null)
                .status(openAccess != null ? openAccess.getStatus().name() : "OUTSIDE")
                .entryTime(openAccess != null ? openAccess.getEntryTime() : null)
                .notes(null)
                .build();
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .plate(vehicle.getPlate())
                .exhibitorId(vehicle.getExhibitor().getId())
                .exhibitorCorporateName(vehicle.getExhibitor().getCorporateName())
                .build();
    }
}
