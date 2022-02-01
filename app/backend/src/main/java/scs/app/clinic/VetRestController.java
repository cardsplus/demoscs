package scs.app.clinic;

import esy.rest.JsonJpaRestControllerBase;
import io.micrometer.core.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.transaction.support.TransactionTemplate;
import scs.api.clinic.Vet;

@RepositoryEventHandler
@BasePathAwareController
public class VetRestController extends JsonJpaRestControllerBase<Vet> {

    @Autowired
    public VetRestController(
            @NonNull final ApplicationEventPublisher eventPublisher,
            @NonNull final TransactionTemplate transactionTemplate) {
        super(eventPublisher, transactionTemplate);
    }
}
