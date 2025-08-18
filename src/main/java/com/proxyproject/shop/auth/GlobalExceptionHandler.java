package com.proxyproject.shop.auth;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

  // === Tus handlers existentes ===

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> validation(MethodArgumentNotValidException ex) {
    var details = ex.getBindingResult().getFieldErrors().stream()
      .collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage, (a,b)->a));
    return Map.of("error","ValidationError","details",details);
  }

  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> notFound(NoSuchElementException ex) {
    return Map.of("error","NotFound","message", ex.getMessage());
  }

  // === Nuevos handlers (NO devolver 401 aquí) ===

  /** Respeta el status original de ResponseStatusException (400/404/409, etc.). */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorBody> handle(ResponseStatusException ex) {
    HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
    if (status == null) status = HttpStatus.BAD_REQUEST;
    String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();
    return ResponseEntity.status(status).body(new ErrorBody(status.value(), message));
  }

  /** IllegalArgumentException -> 400. */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorBody> handle(IllegalArgumentException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorBody(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
  }

  /** Fallback -> 500 sin detalles sensibles. */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorBody> handle(Exception ex) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorBody(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error"));
  }

  /** Cuerpo consistente para respuestas JSON. */
  public static class ErrorBody {
    private final int status;
    private final String message;

    public ErrorBody(int status, String message) {
      this.status = status;
      this.message = message;
    }
    public int getStatus() { return status; }
    public String getMessage() { return message; }
  }
}
