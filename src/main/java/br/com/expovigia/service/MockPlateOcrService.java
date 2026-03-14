package br.com.expovigia.service;

import br.com.expovigia.dto.OcrResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MockPlateOcrService implements PlateOcrService {

    @Override
    public OcrResult extractPlateText(MultipartFile file) {
        // MVP fallback while OCR provider is not integrated.
        // Returning null keeps API behavior honest and enables manual validation flow.
        return new OcrResult(null, 0.0D);
    }
}
