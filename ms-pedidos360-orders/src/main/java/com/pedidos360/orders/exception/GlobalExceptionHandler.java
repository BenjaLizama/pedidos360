package com.pedidos360.orders.exception;

import com.pedidos360.orders.dto.response.StandardResponse;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor global de excepciones para el microservicio de Órdenes.
 * Centraliza la captura de errores para garantizar que el cliente (frontend)
 * siempre reciba una respuesta con la misma estructura JSON (StandardResponse),
 * evitando exponer trazas de stack de Java.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Intercepta errores de validación de los DTOs (anotados con @Valid).
     * Ejemplo: Si el cliente envía una orden sin "clientId" o con cantidad "0",
     * agrupa todos los campos defectuosos y los devuelve en el nodo "data".
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new StandardResponse<>(400, "Error en la validación de los datos enviados", errors));
    }

    /**
     * Intercepta fallos de comunicación con otros microservicios (ej. Catálogo).
     * Si el Catálogo rechaza la petición (ej. 400 por falta de stock o 404 si el producto no existe),
     * Feign lanza esta excepción. Nosotros replicamos el estado HTTP original para no enmascarar
     * el error del microservicio subyacente.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<StandardResponse<String>> handleFeignException(FeignException ex) {
        // Si Feign no logra conectar (Connection Refused), status() devuelve -1.
        // En ese caso forzamos un 500. Si hay respuesta, mantenemos el status del servicio remoto.
        int status = ex.status() == -1 ? HttpStatus.INTERNAL_SERVER_ERROR.value() : ex.status();

        return ResponseEntity.status(status)
                .body(new StandardResponse<>(status, "Fallo en la comunicación con el microservicio de Catálogo: " + ex.getMessage(), null));
    }

    /**
     * Intercepta reglas de negocio rotas de forma controlada.
     * Útil para lanzar errores intencionales (ej. if (monto < 0) throw new BusinessException(...)).
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardResponse<Void>> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new StandardResponse<>(400, ex.getMessage(), null));
    }

    /**
     * Intercepta búsquedas fallidas en la base de datos (Orders).
     * Garantiza que el cliente reciba un código HTTP 404 estándar en lugar de un error 500.
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<StandardResponse<Void>> handleOrderNotFoundException(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new StandardResponse<>(404, ex.getMessage(), null));
    }

    /**
     * Interceptor genérico o "Cajón de sastre".
     * Captura cualquier excepción imprevista (NullPointerException, caída de Base de Datos, etc.)
     * Evita que el cliente vea el stacktrace completo por motivos de seguridad y arquitectura.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse<Void>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new StandardResponse<>(500, "Ocurrió un error interno crítico en el servidor de Órdenes", null));
    }
}