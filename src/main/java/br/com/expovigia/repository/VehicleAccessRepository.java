package br.com.expovigia.repository;

import br.com.expovigia.entity.VehicleAccess;
import br.com.expovigia.enums.VehicleAccessStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleAccessRepository extends JpaRepository<VehicleAccess, Long> {

    boolean existsByPlateAndStatus(String plate, VehicleAccessStatus status);

    Optional<VehicleAccess> findFirstByPlateAndStatusOrderByEntryTimeDesc(String plate, VehicleAccessStatus status);
}
