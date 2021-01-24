package esy.rest;

import esy.json.JsonJpaValueBase;
import io.micrometer.core.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class JsonJpaRestControllerBase<V extends JsonJpaValueBase<V>> {

    private static final Logger LOG = LoggerFactory.getLogger(JsonJpaRestControllerBase.class);

    private final ApplicationEventPublisher eventPublisher;

    private final TransactionTemplate transactionTemplate;

    @ExceptionHandler
    void onIllegalArgumentException(final IllegalArgumentException cause, final HttpServletResponse response) throws IOException {
        LOG.error(cause.getMessage());
        LOG.trace(cause.getMessage(), cause);
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), cause.getMessage());
    }

    @HandleBeforeCreate
    public final void beforeCreate(@NonNull final V value) {
        LOG.info("RECEIVED [{}]; creating ...", value);
        beforeCreateTransaction(value.verify()).ifPresent(transactionTemplate::execute);
        LOG.info("ACCEPTED [{}]", value);
    }

    protected Optional<TransactionCallback<Void>> beforeCreateTransaction(@NonNull final V value) {
        return Optional.empty();
    }

    @HandleAfterCreate
    public final void afterCreate(@NonNull final V value) {
        afterCreateTransaction(value).ifPresent(transactionTemplate::execute);
        eventPublisher.publishEvent(value);
        LOG.info("RELEASED [{}]", value);
    }

    protected Optional<TransactionCallback<Void>> afterCreateTransaction(@NonNull final V value) {
        return Optional.empty();
    }

    @HandleBeforeSave
    public final void beforeSave(@NonNull final V value) {
        LOG.info("RECEIVED [{}]; updating ...", value);
        beforeSaveTransaction(value.verify()).ifPresent(transactionTemplate::execute);
        LOG.info("ACCEPTED [{}]", value);
    }

    protected Optional<TransactionCallback<Void>> beforeSaveTransaction(@NonNull final V value) {
        return Optional.empty();
    }

    @HandleAfterSave
    public final void afterSave(@NonNull final V value) {
        afterSaveTransaction(value).ifPresent(transactionTemplate::execute);
        eventPublisher.publishEvent(value);
        LOG.info("RELEASED [{}]", value);
    }

    protected Optional<TransactionCallback<Void>> afterSaveTransaction(@NonNull final V value) {
        return Optional.empty();
    }

    @HandleBeforeLinkSave
    public final void beforeLinkSave(@NonNull final V value, final Object rel) {
        LOG.info("RECEIVED [{}]; linking with [{}] ...", value, rel);
        beforeLinkSaveTransaction(value.verify()).ifPresent(transactionTemplate::execute);
        LOG.info("ACCEPTED [{}]", value);
    }

    protected Optional<TransactionCallback<Void>> beforeLinkSaveTransaction(@NonNull final V value) {
        return Optional.empty();
    }

    @HandleAfterLinkSave
    public final void afterLinkSave(@NonNull final V value, final Object rel) {
        afterLinkSaveTransaction(value).ifPresent(transactionTemplate::execute);
        eventPublisher.publishEvent(value);
        LOG.info("RELEASED [{}]", value);
    }

    protected Optional<TransactionCallback<Void>> afterLinkSaveTransaction(@NonNull final V value) {
        return Optional.empty();
    }

    @HandleBeforeDelete
    public final void beforeDelete(@NonNull final V value) {
        LOG.info("RECEIVED [{}]; deleting ...", value);
        beforeDeleteTransaction(value.verify()).ifPresent(transactionTemplate::execute);
        LOG.info("ACCEPTED [{}]", value);
    }

    protected Optional<TransactionCallback<Void>> beforeDeleteTransaction(@NonNull final V value) {
        return Optional.empty();
    }

    @HandleAfterDelete
    public final void afterDelete(@NonNull final V value) {
        afterDeleteTransaction(value).ifPresent(transactionTemplate::execute);
        eventPublisher.publishEvent(value);
        LOG.info("RELEASED [{}]", value);
    }

    protected Optional<TransactionCallback<Void>> afterDeleteTransaction(@NonNull final V value) {
        return Optional.empty();
    }

    @HandleBeforeLinkDelete
    public final void beforeLinkDelete(@NonNull final V value, final Object rel) {
        LOG.info("RECEIVED [{}]; unlinking [{}] ...", value, rel);
        beforeLinkDeleteTransaction(value.verify()).ifPresent(transactionTemplate::execute);
        LOG.info("ACCEPTED [{}]", value);
    }

    protected Optional<TransactionCallback<Void>> beforeLinkDeleteTransaction(@NonNull final V value) {
        return Optional.empty();
    }

    @HandleAfterLinkDelete
    public final void afterLinkDelete(@NonNull final V value, final Object rel) {
        afterLinkDeleteTransaction(value).ifPresent(transactionTemplate::execute);
        eventPublisher.publishEvent(value);
        LOG.info("RELEASED [{}]", value);
    }

    protected Optional<TransactionCallback<Void>> afterLinkDeleteTransaction(@NonNull final V value) {
        return Optional.empty();
    }
}
