package scs.app.owner;

import esy.rest.JsonJpaRestControllerBase;
import io.micrometer.core.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.transaction.support.TransactionTemplate;
import scs.api.owner.Owner;

@RepositoryEventHandler
@BasePathAwareController
public class OwnerRestController extends JsonJpaRestControllerBase<Owner> {

    @Autowired
    public OwnerRestController(
            @NonNull final ApplicationEventPublisher eventPublisher,
            @NonNull final TransactionTemplate transactionTemplate) {
        super(eventPublisher, transactionTemplate);
    }
}
