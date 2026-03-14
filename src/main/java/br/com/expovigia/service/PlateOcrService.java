package br.com.expovigia.service;

import br.com.expovigia.dto.PlateOcrRequest;
import br.com.expovigia.dto.PlateOcrResponse;

public interface PlateOcrService {

    PlateOcrResponse extractPlate(PlateOcrRequest request);
}
