package com.mypetadmin.ps_empresa.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EmpresaExistenteException.class)
    public ResponseEntity<ErrorResponse> handleEmpresaExistente(EmpresaExistenteException ex,
                                                                 HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "EMPRESA_ALREADY_EXISTS", ex.getMessage(), request);
    }

    @ExceptionHandler(EmailExistenteException.class)
    public ResponseEntity<ErrorResponse> handleEmailExistente(EmailExistenteException ex,
                                                               HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", ex.getMessage(), request);
    }

    @ExceptionHandler(OnboardingConflictException.class)
    public ResponseEntity<ErrorResponse> handleOnboardingConflict(OnboardingConflictException ex,
                                                                   HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "ONBOARDING_CONFLICT", ex.getMessage(), request);
    }

    @ExceptionHandler(TenantAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleTenantAccessDenied(TenantAccessDeniedException ex,
                                                                   HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "TENANT_ACCESS_DENIED", ex.getMessage(), request);
    }

    @ExceptionHandler(EmpresaNaoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleEmpresaNaoEncontrada(EmpresaNaoEncontradaException ex,
                                                                     HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "EMPRESA_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(CnpjInvalidException.class)
    public ResponseEntity<ErrorResponse> handleCnpjInvalido(CnpjInvalidException ex,
                                                             HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_CNPJ", ex.getMessage(), request);
    }

    @ExceptionHandler(StatusInvalidException.class)
    public ResponseEntity<ErrorResponse> handleStatusInvalido(StatusInvalidException ex,
                                                               HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_STATUS", ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                                HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex,
                                                                 HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        log.warn("validation.failed method={} path={} fields={}", request.getMethod(), request.getRequestURI(), errors.keySet());
        return ResponseEntity.badRequest().body(
                ErrorResponse.validation(
                        "Um ou mais campos são inválidos.",
                        HttpStatus.BAD_REQUEST.value(),
                        request.getRequestURI(),
                        errors
                )
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex,
                                                              HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", "Parâmetro ausente: " + ex.getParameterName(), request);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException ex,
                                                              HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "MISSING_HEADER", "Header ausente: " + ex.getHeaderName(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                       HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST_BODY", "Corpo da requisição ausente ou inválido.", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
                                                              HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "DATA_CONFLICT", "Os dados informados conflitam com um registro existente.", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex,
                                                        HttpServletRequest request) {
        log.error("request.unexpected-error method={} path={}", request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Erro interno no servidor. Tente novamente mais tarde.", request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status,
                                                String code,
                                                String message,
                                                HttpServletRequest request) {
        if (status.is4xxClientError()) {
            log.warn("request.rejected code={} method={} path={}", code, request.getMethod(), request.getRequestURI());
        }
        return ResponseEntity.status(status).body(
                ErrorResponse.of(code, message, status.value(), request.getRequestURI())
        );
    }
}
