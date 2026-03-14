package br.com.expovigia.repository;

import br.com.expovigia.entity.Exhibitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitorRepository extends JpaRepository<Exhibitor, Long> {
}
