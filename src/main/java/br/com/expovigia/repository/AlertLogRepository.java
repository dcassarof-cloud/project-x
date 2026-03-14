package br.com.expovigia.repository;

import br.com.expovigia.entity.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertLogRepository extends JpaRepository<AlertLog, Long> {
}
