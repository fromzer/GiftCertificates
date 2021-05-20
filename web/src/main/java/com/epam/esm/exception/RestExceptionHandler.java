package com.epam.esm.exception;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String CREATE_RESOURCE_MESSAGE = "exception.createResource";
    private static final String RESOURCE_NOT_FOUND_MESSAGE = "exception.resourceNotFound";
    private static final String INVALID_PARAMS_MESSAGE = "exception.invalidParams";
    private static final String RESOURCE_IS_EXIST_MESSAGE = "exception.resourceIsExist";
    private static final String UPDATE_RESOURCE_MESSAGE = "exception.updateResource";
    private static final String TYPE_MISMATCH_MESSAGE = "exception.typeMismatch";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "exception.internalServer";
    private static final String METHOD_NOT_SUPPORTED = "exception.methodNotSupported";
    private static final String NOT_VALID_VALUE = "exception.notValidValue";
    private static final String INVALID_LOGIN = "exception.notValidLogin";
    private static final String TOKEN_NOT_VALID = "exception.tokenNotValid";
    private static final String LOGIN_EXIST_MESSAGE = "exception.loginExist";
    private static final String ACCESS_DENIED = "exception.accessDenied";
    private static final String PASSWORD_MISMATCH = "exception.passwordMismatch";
    private static final String INVALID_PASSWORD = "exception.invalidPassword";

    private final MessageSource messageSource;
    private final Locale defaultLocale = Locale.getDefault();

    @Autowired
    public RestExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(value = UpdateResourceException.class)
    protected ResponseEntity<ErrorMessage> handleUpdateResourceException(Locale locale) {
        String msg = messageSource.getMessage(UPDATE_RESOURCE_MESSAGE, null, locale);
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_MODIFIED.value(), msg, "30414");
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = JwtAuthenticationException.class)
    protected ResponseEntity<ErrorMessage> handleJwtAuthenticationException(Locale locale) {
        String msg = messageSource.getMessage(TOKEN_NOT_VALID, null, locale);
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FORBIDDEN.value(), msg, "40301");
        return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = LoginExistsException.class)
    protected ResponseEntity<ErrorMessage> handleLoginExistsException(Locale locale) {
        String msg = messageSource.getMessage(LOGIN_EXIST_MESSAGE, null, locale);
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), msg, "42201");
        return new ResponseEntity<>(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = UserBlockedException.class)
    protected ResponseEntity<ErrorMessage> handleUserBlockedException(UserBlockedException exception) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FORBIDDEN.value(), exception.getLocalizedMessage(), "40308");
        return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = BadPasswordException.class)
    protected ResponseEntity<ErrorMessage> handleBadPasswordException(Locale locale) {
        String msg = messageSource.getMessage(INVALID_PASSWORD, null, locale);
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FORBIDDEN.value(), msg, "40306");
        return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    protected ResponseEntity<ErrorMessage> handleUserNotFoundException(Locale locale) {
        String msg = messageSource.getMessage(INVALID_LOGIN, null, locale);
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FORBIDDEN.value(), msg, "40309");
        return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    protected ResponseEntity<ErrorMessage> handleAccessDeniedException(Locale locale) {
        String msg = messageSource.getMessage(ACCESS_DENIED, null, locale);
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FORBIDDEN.value(), msg, "40302");
        return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = PasswordMismatchException.class)
    protected ResponseEntity<ErrorMessage> handlePasswordMismatchException(Locale locale) {
        String msg = messageSource.getMessage(PASSWORD_MISMATCH, null, locale);
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.UNAUTHORIZED.value(), msg, "40107");
        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class, EntityRetrievalException.class})
    protected ResponseEntity<ErrorMessage> handleResourceNotFoundException(Locale locale) {
        String msg = messageSource.getMessage(RESOURCE_NOT_FOUND_MESSAGE, null, locale);
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND.value(), msg, "40411");
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    protected ResponseEntity<ErrorMessage> handleInvalidParamsException(Locale locale) {
        String msg = messageSource.getMessage(INVALID_PARAMS_MESSAGE, null, locale);
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), msg, "40011");
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CreateResourceException.class)
    protected ResponseEntity<ErrorMessage> handleCreateResourceException(Locale locale) {
        String msg = messageSource.getMessage(CREATE_RESOURCE_MESSAGE, null, locale);
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), msg, "40010");
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ExistEntityException.class)
    protected ResponseEntity<ErrorMessage> handleExistEntityException(Locale locale) {
        String msg = messageSource.getMessage(RESOURCE_IS_EXIST_MESSAGE, null, locale);
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.CONFLICT.value(), msg, "40910");
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<ErrorMessage> handleEntityException(Locale locale) {
        String msg = messageSource.getMessage(INTERNAL_SERVER_ERROR_MESSAGE, null, locale);
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, "50010");
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String msg = messageSource.getMessage(TYPE_MISMATCH_MESSAGE, null, defaultLocale);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("errorMessage", msg);
        body.put("errorCode", status.value() + "" + (status.value() / 10 + 11));
        return new ResponseEntity<>(body, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("HTTP Status", status.value());
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .map(s -> messageSource.getMessage(s, null, defaultLocale))
                .collect(Collectors.toList());
        body.put("errorMessage", errors);
        body.put("errorCode", status.value() + "" + (status.value() / 10));
        return new ResponseEntity<>(body, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String msg = messageSource.getMessage(TYPE_MISMATCH_MESSAGE, null, defaultLocale);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("errorMessage", msg);
        body.put("errorCode", status.value() + "" + (status.value() / 10 + 14));
        return new ResponseEntity<>(body, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String msg = messageSource.getMessage(RESOURCE_NOT_FOUND_MESSAGE, null, defaultLocale);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("errorMessage", msg);
        body.put("errorCode", status.value() + "" + (status.value() / 10));
        return new ResponseEntity<>(body, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String msg = messageSource.getMessage(METHOD_NOT_SUPPORTED, null, defaultLocale);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("errorMessage", msg);
        body.put("errorCode", status.value() + "" + (status.value() / 10 + 18));
        return new ResponseEntity<>(body, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String msg = messageSource.getMessage(NOT_VALID_VALUE, null, defaultLocale);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("errorMessage", msg);
        body.put("errorCode", status.value() + "" + (status.value() / 10 + 20));
        return new ResponseEntity<>(body, headers, status);
    }
}
