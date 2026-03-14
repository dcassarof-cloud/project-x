package br.com.expovigia.service;

import br.com.expovigia.dto.CameraReadRequest;
import br.com.expovigia.dto.VehicleAccessResponse;
import br.com.expovigia.dto.VehicleEntryRequest;
import br.com.expovigia.dto.VehicleExitRequest;
import br.com.expovigia.enums.CameraDirection;
import br.com.expovigia.util.PlateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CameraService {

    private final VehicleAccessService vehicleAccessService;

    public VehicleAccessResponse read(CameraReadRequest request) {
        String normalizedPlate = PlateUtils.normalize(request.getPlate());

        if (request.getDirection() == CameraDirection.ENTRY) {
            VehicleEntryRequest entryRequest = new VehicleEntryRequest(normalizedPlate, request.getGate());
            return vehicleAccessService.registerEntry(entryRequest);
        }

        VehicleExitRequest exitRequest = new VehicleExitRequest(normalizedPlate);
        return vehicleAccessService.registerExit(exitRequest);
    }
}
