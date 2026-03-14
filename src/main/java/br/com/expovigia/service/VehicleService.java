package br.com.expovigia.service;

import br.com.expovigia.dto.CreateVehicleRequest;
import br.com.expovigia.dto.VehicleResponse;
import br.com.expovigia.entity.Exhibitor;
import br.com.expovigia.entity.Vehicle;
import br.com.expovigia.exception.ConflictException;
import br.com.expovigia.exception.ResourceNotFoundException;
import br.com.expovigia.repository.ExhibitorRepository;
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

    public VehicleResponse findByPlate(String plate) {
        String normalizedPlate = PlateUtils.normalize(plate);

        Vehicle vehicle = vehicleRepository.findByPlate(normalizedPlate)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle not found with plate: " + normalizedPlate
                ));

        return toResponse(vehicle);
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
