package br.com.expovigia.repository;

import br.com.expovigia.entity.VehicleAccess;
import br.com.expovigia.enums.VehicleAccessStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleAccessRepository extends JpaRepository<VehicleAccess, Long> {

    boolean existsByPlateAndStatus(String plate, VehicleAccessStatus status);

    Optional<VehicleAccess> findFirstByPlateAndStatusOrderByEntryTimeDesc(String plate, VehicleAccessStatus status);

    long countByStatus(VehicleAccessStatus status);

    @Query("""
            select count(va)
            from VehicleAccess va
            where va.entryTime >= :start and va.entryTime < :end
            """)
    long countEntriesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
            select count(va)
            from VehicleAccess va
            where va.exitTime >= :start and va.exitTime < :end
            """)
    long countExitsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
