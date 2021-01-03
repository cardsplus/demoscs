package esy.app.plan;

import esy.api.plan.AufgabeValue;
import io.micrometer.core.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RepositoryEventHandler
@BasePathAwareController
@RequiredArgsConstructor
public class AufgabeValueRestController {

    private static final Logger LOG = LoggerFactory.getLogger(AufgabeValueRestController.class);

    private final ApplicationEventPublisher publisher;

    @ExceptionHandler
    void onIllegalArgumentException(final IllegalArgumentException cause, final HttpServletResponse response) throws IOException {
        LOG.error(cause.getMessage());
        LOG.trace(cause.getMessage(), cause);
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), cause.getMessage());
    }

    @HandleBeforeCreate
    @HandleBeforeSave
    public void onHandleBefore(@NonNull final AufgabeValue value) {
        LOG.info("RECEIVED [{}]", value);
        value.verify();
        LOG.info("ACCEPTED [{}]", value);
    }

    @HandleAfterCreate
    @HandleAfterSave
    public void onHandleAfter(@NonNull final AufgabeValue value) {
        publisher.publishEvent(value);
        LOG.info("RELEASED [{}]", value);
    }
}
