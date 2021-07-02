package esy.app.plan;

import esy.api.plan.ProjektValue;
import esy.rest.JsonJpaRestControllerBase;
import io.micrometer.core.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@RepositoryEventHandler
@BasePathAwareController
public class ProjektValueRestController extends JsonJpaRestControllerBase<ProjektValue> {

    @Autowired
    public ProjektValueRestController(
            @NonNull final ApplicationEventPublisher eventPublisher,
            @NonNull final TransactionTemplate transactionTemplate) {
        super(eventPublisher, transactionTemplate);
    }

    @Override
    protected Optional<TransactionCallback<Void>> beforeLinkDeleteTransaction(final ProjektValue value, final Object rel) {
        if (value.getBesitzer() == null) {
            throw new DataIntegrityViolationException("besitzer is null");
        }
        return Optional.empty();
    }
}
