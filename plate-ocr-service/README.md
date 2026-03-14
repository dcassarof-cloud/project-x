# Plate OCR Service

Microserviço OCR de placas para integração com backend Spring Boot.

- Framework: FastAPI
- Pipeline de imagem: OpenCV
- OCR padrão: Tesseract (funcional por padrão)
- OCR opcional: PaddleOCR (configurável por variável de ambiente)

## Estrutura

```text
plate-ocr-service/
  app/
    main.py
    api.py
    schemas.py
    config.py
    services/
      image_preprocess.py
      plate_ocr.py
      plate_normalizer.py
      candidate_generator.py
      plate_detector.py
  requirements.txt
  Dockerfile
  README.md
```

## Execução local

```bash
cd plate-ocr-service
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --host 0.0.0.0 --port 8001
```

> Requisito no host: `tesseract-ocr` instalado (Ubuntu/Debian: `sudo apt-get install tesseract-ocr`).

## Endpoints

### Healthcheck

```bash
curl http://localhost:8001/health
```

Resposta:

```json
{ "status": "ok" }
```

### OCR de placa

```bash
curl -X POST http://localhost:8001/ocr/plate \
  -F "file=@./samples/carro.jpg"
```

Resposta (exemplo):

```json
{
  "rawText": "ABC1Z34",
  "normalizedPlate": "ABC1234",
  "confidence": 0.84,
  "plateRegionDetected": true,
  "candidates": [
    { "text": "ABC1234", "normalizedText": "ABC1234", "confidence": 0.91 },
    { "text": "ABC1Z34", "normalizedText": "ABC1Z34", "confidence": 0.84 }
  ],
  "debug": {
    "engine": "tesseract",
    "candidateCount": 2,
    "regionsDetected": 1,
    "elapsedMs": 78
  }
}
```

## Variáveis de ambiente

Prefixo: `PLATE_OCR_`

- `PLATE_OCR_HOST` (default: `0.0.0.0`)
- `PLATE_OCR_PORT` (default: `8001`)
- `PLATE_OCR_LOG_LEVEL` (default: `INFO`)
- `PLATE_OCR_OCR_ENGINE` (`tesseract` ou `paddle`, default: `tesseract`)
- `PLATE_OCR_PADDLE_LANGUAGE` (default: `en`)
- `PLATE_OCR_MIN_CANDIDATE_CONFIDENCE` (default: `0.20`)
- `PLATE_OCR_MAX_CANDIDATES` (default: `5`)
- `PLATE_OCR_MAX_REGIONS` (default: `4`)
- `PLATE_OCR_ADAPTIVE_THRESHOLD_BLOCK_SIZE` (default: `19`)
- `PLATE_OCR_ADAPTIVE_THRESHOLD_C` (default: `3`)

## PaddleOCR (opcional)

O serviço funciona sem PaddleOCR. Para habilitar:

```bash
pip install paddleocr paddlepaddle
export PLATE_OCR_OCR_ENGINE=paddle
```

Se o PaddleOCR não iniciar corretamente, o serviço faz fallback automático para Tesseract.

## Docker

Build:

```bash
cd plate-ocr-service
docker build -t plate-ocr-service:local .
```

Run:

```bash
docker run --rm -p 8001:8001 \
  -e PLATE_OCR_OCR_ENGINE=tesseract \
  plate-ocr-service:local
```

## Integração com backend Spring

Backend Java esperado:

```properties
app.plate-ocr.base-url=http://localhost:8001
app.plate-ocr.endpoint=/ocr/plate
app.plate-ocr.connect-timeout-ms=3000
app.plate-ocr.read-timeout-ms=10000
```

## Pipeline implementado

1. Decode de bytes multipart para imagem OpenCV.
2. Conversão para escala de cinza.
3. Equalização de histograma (contraste).
4. Redução de ruído (bilateral filter).
5. Adaptive threshold + operação morfológica.
6. Detecção de ROIs prováveis de placa por contornos e razão retangular.
7. OCR nas ROIs + fallback OCR na imagem completa.
8. Normalização de placa (remove espaços/hífens/caracteres não alfanuméricos, uppercase).
9. Consolidação e deduplicação por mapeamento de ambiguidade (`0/O`, `1/I`, `2/Z`, `5/S`, `8/B`).
