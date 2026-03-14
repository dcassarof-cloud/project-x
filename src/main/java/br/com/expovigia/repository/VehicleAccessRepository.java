package br.com.expovigia.repository;

import br.com.expovigia.entity.VehicleAccess;
import br.com.expovigia.enums.VehicleAccessStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleAccessRepository extends JpaRepository<VehicleAccess, Long> {

    List<VehicleAccess> findByPlateAndStatus(String plate, VehicleAccessStatus status);
}
