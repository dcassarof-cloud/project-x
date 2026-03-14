package br.com.expovigia.entity;

import br.com.expovigia.enums.PersonFlowType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "person_flow",
    indexes = {
        @Index(name = "idx_person_flow_recorded_at", columnList = "recorded_at"),
        @Index(name = "idx_person_flow_type", columnList = "type"),
        @Index(name = "idx_person_flow_gate", columnList = "gate")
    }
)
public class PersonFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private PersonFlowType type;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "gate", nullable = false, length = 50)
    private String gate;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "source", nullable = false, length = 100)
    private String source;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (recordedAt == null) {
            this.recordedAt = now;
        }
        this.createdAt = now;
    }
}
