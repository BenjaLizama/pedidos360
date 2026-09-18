package com.pedidos360.catalog.mapper;

import com.pedidos360.catalog.dto.response.StandardErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@Component
public class ErrorMapper {

    public StandardErrorResponse toValidationResponse(MethodArgumentNotValidException ex, String path) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        return StandardErrorResponse.builder()
                .timestamp(System.currentTimeMillis())
                .status(HttpStatus.BAD_REQUEST.value())
                .code("VAL_400")
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Error de validación en los datos enviados.")
                .developerMessage("Validación fallida para " + validationErrors.size() + " campos.")
                .path(path)
                .validationError(validationErrors)
                .build();
    }

    public StandardErrorResponse toGenericResponse(HttpStatus status, String code, String message, String devMessage, String path) {
        return StandardErrorResponse.builder()
                .timestamp(System.currentTimeMillis())
                .status(status.value())
                .code(code)
                .error(status.getReasonPhrase())
                .message(message)
                .developerMessage(devMessage)
                .path(path)
                .build();
    }
}
