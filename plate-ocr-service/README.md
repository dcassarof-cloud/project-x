# Plate OCR Service

Microserviço OCR de placas com FastAPI + OpenCV + Tesseract.

## Executar local

```bash
cd plate-ocr-service
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8090 --reload
```

## Endpoints

- `GET /health`
- `POST /ocr/plate` (multipart file: `file`)

## Exemplo de resposta

```json
{
  "rawText": "ABC1Z34",
  "normalizedPlate": "ABC1Z34",
  "confidence": 0.84,
  "plateRegionDetected": true,
  "candidates": [
    { "text": "ABC1234", "normalizedText": "ABC1234", "confidence": 0.91 },
    { "text": "ABC1Z34", "normalizedText": "ABC1Z34", "confidence": 0.84 }
  ]
}
```
