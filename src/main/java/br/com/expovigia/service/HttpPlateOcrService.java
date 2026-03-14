package br.com.expovigia.service;

import br.com.expovigia.dto.PlateOcrRequest;
import br.com.expovigia.dto.PlateOcrResponse;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@Slf4j
public class HttpPlateOcrService implements PlateOcrService {

    private final RestClient restClient;
    private final String endpointPath;

    public HttpPlateOcrService(
            RestClient.Builder restClientBuilder,
            @Value("${app.plate-ocr.base-url:${ocr.client.base-url:http://localhost:8090}}") String baseUrl,
            @Value("${app.plate-ocr.endpoint:${ocr.client.endpoint:/ocr/plate}}") String endpointPath,
            @Value("${app.plate-ocr.connect-timeout-ms:${ocr.client.connect-timeout-ms:3000}}") int connectTimeoutMs,
            @Value("${app.plate-ocr.read-timeout-ms:${ocr.client.read-timeout-ms:10000}}") int readTimeoutMs
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);
        requestFactory.setReadTimeout(readTimeoutMs);

        this.restClient = restClientBuilder
                .requestFactory(requestFactory)
                .baseUrl(baseUrl)
                .build();
        this.endpointPath = endpointPath;
    }

    @Override
    public PlateOcrResponse extractPlate(PlateOcrRequest request) {
        long start = System.currentTimeMillis();

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(request.imageBytes()) {
                @Override
                public String getFilename() {
                    return request.fileName() == null ? "camera-capture.jpg" : request.fileName();
                }
            });

            log.info("Enviando imagem para OCR. fileName={} sizeBytes={}", request.fileName(), request.imageBytes().length);

            PlateOcrResponse response = restClient.post()
                    .uri(endpointPath)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(PlateOcrResponse.class);

            long elapsed = System.currentTimeMillis() - start;
            if (response == null) {
                log.warn("OCR retornou resposta vazia em {}ms", elapsed);
                return PlateOcrResponse.empty();
            }

            log.info(
                    "OCR concluído em {}ms. rawText={} confidence={} candidates={}",
                    elapsed,
                    sanitize(response.rawText()),
                    response.confidence(),
                    response.candidates() == null ? 0 : response.candidates().size()
            );
            return response;
        } catch (RestClientException ex) {
            throw new OcrServiceUnavailableException("Falha ao consumir serviço OCR", ex);
        }
    }

    private String sanitize(String value) {
        if (value == null) {
            return null;
        }

        return new String(value.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8)
                .replaceAll("[^A-Za-z0-9]", "");
    }
}
