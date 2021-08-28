package esy.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Slf4j
public class EndpointControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            ResourceAccessException.class
    })
    public void handleNotFound(final Exception cause, final HttpServletResponse response) throws IOException {
        log.error(cause.toString());
        log.trace(cause.toString(), cause);
        response.sendError(HttpStatus.NOT_FOUND.value(), cause.getMessage());
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            NullPointerException.class,
            UnsupportedOperationException.class
    })
    public void handleError(final Exception cause, final HttpServletResponse response) throws IOException {
        log.error(cause.toString());
        log.trace(cause.toString(), cause);
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), cause.getMessage());
    }
}
