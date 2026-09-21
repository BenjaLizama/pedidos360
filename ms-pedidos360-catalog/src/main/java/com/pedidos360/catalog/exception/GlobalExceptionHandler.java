package com.pedidos360.catalog.exception;

import com.pedidos360.catalog.dto.response.StandardErrorResponse;
import com.pedidos360.catalog.mapper.ErrorMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorMapper errorMapper;

    /**
     * Captura errores de validación en los formularios o JSONs de entrada.
     * Se dispara AUTOMÁTICAMENTE por Spring Boot antes de llegar al Servicio,
     * cuando un DTO no cumple con las reglas definidas (@NotBlank, @Min, @NotNull).
     * Requiere que el parámetro del controlador tenga la anotación @Valid.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        StandardErrorResponse response = errorMapper.toValidationResponse(ex, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Captura búsquedas fallidas de registros en el sistema.
     * Se dispara MANUALMENTE desde la capa de Servicio.
     * Ejemplo de uso: repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(...))
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {

        StandardErrorResponse response = errorMapper.toGenericResponse(
                HttpStatus.NOT_FOUND,
                "RES_404",
                ex.getMessage(),
                "Recurso no encontrado en BD.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Captura cualquier otro error no contemplado (El "Cajón de sastre").
     * Se dispara AUTOMÁTICAMENTE cuando el código explota por un error inesperado
     * (Ej: NullPointerException, división por cero, caída de la base de datos).
     * Evita que el usuario vea la traza de código del servidor (Stacktrace).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardErrorResponse> handleAllUncaughtException(
            Exception ex, HttpServletRequest request) {

        log.error("Error critico no controlado: ", ex);

        StandardErrorResponse response = errorMapper.toGenericResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "SYS_500",
                "Ocurrió un error inesperado en el servidor.",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Captura errores relacionados con el cuerpo de la petición (Payload).
     * Se dispara AUTOMÁTICAMENTE por Spring Boot cuando el cliente no envía el JSON requerido en un POST/PUT,
     * o cuando el JSON enviado tiene un formato inválido (ej. comas faltantes, texto donde va un número).
     * Evita que el servidor devuelva un error 500 al fallar la lectura (parseo) de Jackson.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        StandardErrorResponse response = errorMapper.toGenericResponse(
                HttpStatus.BAD_REQUEST,
                "REQ_400",
                "El cuerpo de la petición (JSON) está ausente o mal formado.",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Captura peticiones que utilizan un verbo HTTP incorrecto para una ruta existente.
     * Se dispara AUTOMÁTICAMENTE por Spring Boot cuando un cliente intenta acceder a un endpoint válido,
     * pero utilizando un método no soportado (ej. lanzar un GET a una ruta que solo acepta POST).
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        StandardErrorResponse response = errorMapper.toGenericResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "MET_405",
                "El método HTTP utilizado no está soportado en esta ruta.",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * Captura operaciones de inventario que intentan exceder el stock disponible.
     * Devuelve un código HTTP 400 (Bad Request) para informar al cliente o al
     * servicio de órdenes que la cantidad solicitada es inválida y la transacción fue rechazada.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<StandardErrorResponse> handleInsufficientStock(
            InsufficientStockException ex, HttpServletRequest request) {

        StandardErrorResponse response = errorMapper.toGenericResponse(
                HttpStatus.BAD_REQUEST, // Código 400
                "STK_400",
                "Inventario insuficiente para procesar la orden.",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
