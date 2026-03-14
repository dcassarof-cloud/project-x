package br.com.expovigia.repository;

import br.com.expovigia.entity.PersonFlow;
import br.com.expovigia.enums.PersonFlowType;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonFlowRepository extends JpaRepository<PersonFlow, Long> {

    interface PersonFlowTotalsProjection {
        Long getEntries();

        Long getExits();
    }

    @Query("""
            select
                coalesce(sum(case when pf.type = br.com.expovigia.enums.PersonFlowType.ENTRY then pf.quantity else 0 end), 0) as entries,
                coalesce(sum(case when pf.type = br.com.expovigia.enums.PersonFlowType.EXIT then pf.quantity else 0 end), 0) as exits
            from PersonFlow pf
            """)
    PersonFlowTotalsProjection findTotals();

    @Query("""
            select
                coalesce(sum(case when pf.type = br.com.expovigia.enums.PersonFlowType.ENTRY then pf.quantity else 0 end), 0) as entries,
                coalesce(sum(case when pf.type = br.com.expovigia.enums.PersonFlowType.EXIT then pf.quantity else 0 end), 0) as exits
            from PersonFlow pf
            where pf.recordedAt >= :start and pf.recordedAt < :end
            """)
    PersonFlowTotalsProjection findTotalsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
            select pf
            from PersonFlow pf
            where (:type is null or pf.type = :type)
              and (:gate is null or lower(pf.gate) = lower(:gate))
              and (:from is null or pf.recordedAt >= :from)
              and (:to is null or pf.recordedAt <= :to)
            """)
    Page<PersonFlow> findRecentWithFilters(
            @Param("type") PersonFlowType type,
            @Param("gate") String gate,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );
}
