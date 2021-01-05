package esy.app.plan;

import esy.api.plan.ProjektValue;
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
public class ProjektValueRestController {

    private static final Logger LOG = LoggerFactory.getLogger(ProjektValueRestController.class);

    private final ApplicationEventPublisher publisher;

    @ExceptionHandler
    void onIllegalArgumentException(final IllegalArgumentException cause, final HttpServletResponse response) throws IOException {
        LOG.error(cause.getMessage());
        LOG.trace(cause.getMessage(), cause);
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), cause.getMessage());
    }

    @HandleBeforeCreate
    public void beforeCreate(@NonNull final ProjektValue value) {
        LOG.info("RECEIVED [{}]", value);
        value.verify();
        LOG.info("ACCEPTED [{}]", value);
    }

    @HandleAfterCreate
    public void afterCreate(@NonNull final ProjektValue value) {
        publisher.publishEvent(value);
        LOG.info("RELEASED [{}]", value);
    }

    @HandleBeforeSave
    public void beforeSave(@NonNull final ProjektValue value) {
        LOG.info("RECEIVED [{}]", value);
        value.verify();
        LOG.info("ACCEPTED [{}]", value);
    }

    @HandleAfterSave
    public void afterSave(@NonNull final ProjektValue value) {
        publisher.publishEvent(value);
        LOG.info("RELEASED [{}]", value);
    }

    @HandleBeforeLinkSave
    public void beforeLinkSave(@NonNull final ProjektValue value, final Object rel) {
        LOG.info("RECEIVED [{}]", value);
        value.verify();
        LOG.info("ACCEPTED [{}]", value);
    }

    @HandleAfterLinkSave
    public void afterLinkSave(@NonNull final ProjektValue value, final Object rel) {
        publisher.publishEvent(value);
        LOG.info("RELEASED [{}]", value);
    }

    @HandleBeforeDelete
    public void beforeDelete(@NonNull final ProjektValue value) {
        LOG.info("RECEIVED [{}]", value);
        value.verify();
        LOG.info("ACCEPTED [{}]", value);
    }

    @HandleAfterDelete
    public void afterDelete(@NonNull final ProjektValue value) {
        publisher.publishEvent(value);
        LOG.info("RELEASED [{}]", value);
    }

    @HandleBeforeLinkDelete
    public void beforeLinkDelete(@NonNull final ProjektValue value, final Object rel) {
        LOG.info("RECEIVED [{}]", value);
        value.verify();
        LOG.info("ACCEPTED [{}]", value);
    }

    @HandleAfterLinkDelete
    public void afterLinkDelete(@NonNull final ProjektValue value, final Object rel) {
        publisher.publishEvent(value);
        LOG.info("RELEASED [{}]", value);
    }
}
