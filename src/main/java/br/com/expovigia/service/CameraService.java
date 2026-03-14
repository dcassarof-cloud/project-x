package br.com.expovigia.service;

import br.com.expovigia.dto.CameraConfirmRequest;
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
        return process(request.getPlate(), request.getGate(), request.getDirection());
    }

    public VehicleAccessResponse confirm(CameraConfirmRequest request) {
        return process(request.getPlate(), request.getGate(), request.getDirection());
    }

    private VehicleAccessResponse process(String plate, String gate, CameraDirection direction) {
        String normalizedPlate = PlateUtils.normalize(plate);

        if (direction == CameraDirection.ENTRY) {
            VehicleEntryRequest entryRequest = new VehicleEntryRequest(normalizedPlate, gate);
            return vehicleAccessService.registerEntry(entryRequest);
        }

        VehicleExitRequest exitRequest = new VehicleExitRequest(normalizedPlate);
        return vehicleAccessService.registerExit(exitRequest);
    }
}
