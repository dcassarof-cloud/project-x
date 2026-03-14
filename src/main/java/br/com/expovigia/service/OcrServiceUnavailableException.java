package br.com.expovigia.service;

public class OcrServiceUnavailableException extends RuntimeException {

    public OcrServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
