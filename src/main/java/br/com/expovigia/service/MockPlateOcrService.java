package br.com.expovigia.service;

import br.com.expovigia.dto.OcrResult;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MockPlateOcrService implements PlateOcrService {

    @Override
    public OcrResult extractPlateText(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            return new OcrResult("ABC1D23", 0.55D);
        }

        String normalizedName = filename.toUpperCase(Locale.ROOT);
        String candidate = normalizedName.replaceAll("[^A-Z0-9]", "");

        if (candidate.length() >= 7) {
            return new OcrResult(candidate.substring(0, 7), 0.70D);
        }

        return new OcrResult("ABC1D23", 0.55D);
    }
}
