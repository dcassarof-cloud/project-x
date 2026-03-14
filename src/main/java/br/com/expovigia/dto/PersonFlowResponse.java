package br.com.expovigia.dto;

import br.com.expovigia.enums.PersonFlowType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PersonFlowResponse(
        Long id,
        PersonFlowType type,
        Integer quantity,
        String gate,
        String source,
        LocalDateTime recordedAt
) {
}
