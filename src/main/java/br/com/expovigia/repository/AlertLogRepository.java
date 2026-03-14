package br.com.expovigia.repository;

import br.com.expovigia.entity.AlertLog;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertLogRepository extends JpaRepository<AlertLog, Long> {

    long countBySentAtBetween(LocalDateTime start, LocalDateTime end);
}
