package br.com.expovigia.service;

import br.com.expovigia.dto.CreateExhibitorRequest;
import br.com.expovigia.dto.ExhibitorResponse;
import br.com.expovigia.entity.Exhibitor;
import br.com.expovigia.exception.ResourceNotFoundException;
import br.com.expovigia.repository.ExhibitorRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitorService {

    private final ExhibitorRepository exhibitorRepository;

    public ExhibitorResponse create(CreateExhibitorRequest request) {
        Exhibitor exhibitor = Exhibitor.builder()
                .cnpj(request.getCnpj())
                .corporateName(request.getCorporateName())
                .responsibleName(request.getResponsibleName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .area(request.getArea())
                .build();

        Exhibitor savedExhibitor = exhibitorRepository.save(exhibitor);
        return toResponse(savedExhibitor);
    }

    public List<ExhibitorResponse> findAll() {
        return exhibitorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ExhibitorResponse findById(Long id) {
        Exhibitor exhibitor = exhibitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exhibitor not found with id: " + id));

        return toResponse(exhibitor);
    }

    private ExhibitorResponse toResponse(Exhibitor exhibitor) {
        return ExhibitorResponse.builder()
                .id(exhibitor.getId())
                .cnpj(exhibitor.getCnpj())
                .corporateName(exhibitor.getCorporateName())
                .responsibleName(exhibitor.getResponsibleName())
                .phone(exhibitor.getPhone())
                .email(exhibitor.getEmail())
                .area(exhibitor.getArea())
                .build();
    }
}
