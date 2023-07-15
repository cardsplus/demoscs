package esy.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.*;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RestController
public class EndpointControllerAdvice extends ResponseEntityExceptionHandler implements ErrorController {

    @ExceptionHandler({
            MissingResourceException.class,
            NoSuchElementException.class,
            DataRetrievalFailureException.class,
            ResourceAccessException.class,
            EntityNotFoundException.class
    })
    public ResponseEntity<Object> handleNotFound(final WebRequest request, final Exception cause) {
        final var error = new ResponseStatusException(HttpStatus.NOT_FOUND, cause.getMessage(), cause);
        return handleErrorResponseException(error, error.getHeaders(), error.getStatusCode(), request);
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class,
            ConcurrencyFailureException.class,
            EntityExistsException.class,
            OptimisticLockException.class,
            PessimisticLockException.class
    })
    public ResponseEntity<Object> handleConflict(final WebRequest request, final Exception cause) {
        final var error = new ResponseStatusException(HttpStatus.CONFLICT, cause.getMessage(), cause);
        return handleErrorResponseException(error, error.getHeaders(), error.getStatusCode(), request);
    }

    @ExceptionHandler({
            NumberFormatException.class,
            DataAccessException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<Object> handleBadRequest(final WebRequest request, final Exception cause) {
        final var error = new ResponseStatusException(HttpStatus.BAD_REQUEST, cause.getMessage(), cause);
        return handleErrorResponseException(error, error.getHeaders(), error.getStatusCode(), request);
    }

    @ExceptionHandler({
            NullPointerException.class,
            UnsupportedOperationException.class,
            BadSqlGrammarException.class
    })
    public ResponseEntity<Object> handleInternalServerError(final WebRequest request, final Exception cause) {
        final var error = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, cause.getMessage(), cause);
        return handleErrorResponseException(error, error.getHeaders(), error.getStatusCode(), request);
    }

    @RequestMapping("${server.error.path:/error}")
    public ResponseEntity<Object> error(final WebRequest request) {
        final var error = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        return handleErrorResponseException(error, error.getHeaders(), error.getStatusCode(), request);
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        return ResponseEntity
                .status(statusCode)
                .headers(headers)
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
