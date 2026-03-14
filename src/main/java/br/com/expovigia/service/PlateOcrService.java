package br.com.expovigia.service;

import br.com.expovigia.dto.OcrResult;
import org.springframework.web.multipart.MultipartFile;

public interface PlateOcrService {

    OcrResult extractPlateText(MultipartFile file);
}
