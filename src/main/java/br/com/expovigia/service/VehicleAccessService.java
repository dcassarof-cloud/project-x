package br.com.expovigia.service;

import br.com.expovigia.dto.VehicleAccessResponse;
import br.com.expovigia.dto.VehicleEntryRequest;
import br.com.expovigia.dto.VehicleExitRequest;
import br.com.expovigia.entity.Vehicle;
import br.com.expovigia.entity.VehicleAccess;
import br.com.expovigia.enums.VehicleAccessStatus;
import br.com.expovigia.exception.BusinessException;
import br.com.expovigia.exception.ConflictException;
import br.com.expovigia.exception.ResourceNotFoundException;
import br.com.expovigia.repository.VehicleAccessRepository;
import br.com.expovigia.repository.VehicleRepository;
import br.com.expovigia.util.PlateUtils;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleAccessService {

    private final VehicleRepository vehicleRepository;
    private final VehicleAccessRepository vehicleAccessRepository;

    public VehicleAccessResponse registerEntry(VehicleEntryRequest request) {
        String normalizedPlate = PlateUtils.normalize(request.getPlate());

        Vehicle vehicle = vehicleRepository.findByPlate(normalizedPlate)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle not found with plate: " + normalizedPlate
                ));

        if (vehicleAccessRepository.existsByPlateAndStatus(normalizedPlate, VehicleAccessStatus.INSIDE)) {
            throw new ConflictException("Vehicle already has an open access for plate: " + normalizedPlate);
        }

        VehicleAccess access = VehicleAccess.builder()
                .vehicle(vehicle)
                .plate(normalizedPlate)
                .gate(request.getGate())
                .entryTime(LocalDateTime.now())
                .status(VehicleAccessStatus.INSIDE)
                .build();

        VehicleAccess savedAccess = vehicleAccessRepository.save(access);
        return toResponse(savedAccess);
    }

    public VehicleAccessResponse registerExit(VehicleExitRequest request) {
        String normalizedPlate = PlateUtils.normalize(request.getPlate());

        VehicleAccess access = vehicleAccessRepository
                .findFirstByPlateAndStatusOrderByEntryTimeDesc(normalizedPlate, VehicleAccessStatus.INSIDE)
                .orElseThrow(() -> new BusinessException(
                        "No open vehicle access found for plate: " + normalizedPlate
                ));

        access.setExitTime(LocalDateTime.now());
        access.setStatus(VehicleAccessStatus.EXITED);

        VehicleAccess savedAccess = vehicleAccessRepository.save(access);
        return toResponse(savedAccess);
    }

    private VehicleAccessResponse toResponse(VehicleAccess vehicleAccess) {
        return VehicleAccessResponse.builder()
                .id(vehicleAccess.getId())
                .plate(vehicleAccess.getPlate())
                .gate(vehicleAccess.getGate())
                .entryTime(vehicleAccess.getEntryTime())
                .exitTime(vehicleAccess.getExitTime())
                .status(vehicleAccess.getStatus())
                .build();
    }
}
