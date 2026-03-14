package br.com.expovigia.service;

import br.com.expovigia.dto.PlateCandidateResponse;
import br.com.expovigia.entity.Vehicle;
import br.com.expovigia.repository.VehicleRepository;
import br.com.expovigia.util.PlateUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlateMatchingService {

    private static final int MAX_DISTANCE = 1;

    private final VehicleRepository vehicleRepository;

    public Optional<Vehicle> findBestMatch(String normalizedPlate, List<PlateCandidateResponse> candidates) {
        List<String> probes = new ArrayList<>();
        if (normalizedPlate != null) {
            probes.add(normalizedPlate);
        }

        if (candidates != null) {
            probes.addAll(candidates.stream().map(PlateCandidateResponse::normalizedText).toList());
            probes.addAll(candidates.stream().map(PlateCandidateResponse::text).toList());
        }

        List<String> normalizedProbes = probes.stream()
                .map(PlateUtils::normalize)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .toList();

        if (normalizedProbes.isEmpty()) {
            return Optional.empty();
        }

        for (String probe : normalizedProbes) {
            Optional<Vehicle> exact = vehicleRepository.findByPlate(probe);
            if (exact.isPresent()) {
                log.info("Match exato de placa encontrado para {}", probe);
                return exact;
            }
        }

        List<Vehicle> vehicles = vehicleRepository.findAll();

        return vehicles.stream()
                .min(Comparator.comparingInt(vehicle -> bestDistance(vehicle.getPlate(), normalizedProbes)))
                .filter(vehicle -> bestDistance(vehicle.getPlate(), normalizedProbes) <= MAX_DISTANCE)
                .map(vehicle -> {
                    log.info("Match aproximado encontrado. vehiclePlate={} distance={}",
                            vehicle.getPlate(), bestDistance(vehicle.getPlate(), normalizedProbes));
                    return vehicle;
                });
    }

    private int bestDistance(String registeredPlate, List<String> probes) {
        String canonicalRegistered = PlateUtils.canonicalizeAmbiguousCharacters(registeredPlate);

        return probes.stream()
                .map(PlateUtils::canonicalizeAmbiguousCharacters)
                .filter(value -> value != null)
                .mapToInt(probe -> PlateUtils.levenshteinDistance(canonicalRegistered, probe))
                .min()
                .orElse(Integer.MAX_VALUE);
    }
}
