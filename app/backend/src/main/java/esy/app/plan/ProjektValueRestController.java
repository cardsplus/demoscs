package esy.app.plan;

import esy.api.plan.ProjektItem;
import esy.api.plan.ProjektValue;
import esy.rest.JsonJpaRestControllerBase;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RepositoryEventHandler
@BasePathAwareController
public class ProjektValueRestController extends JsonJpaRestControllerBase<ProjektValue> {

    private final ProjektValueRepository projektValueRepository;

    @Autowired
    public ProjektValueRestController(
            @NonNull final ApplicationEventPublisher eventPublisher,
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final ProjektValueRepository projektValueRepository) {
        super(eventPublisher, transactionTemplate);
        this.projektValueRepository = projektValueRepository;
    }

    @Override
    protected Optional<TransactionCallback<Void>> beforeLinkDeleteTransaction(final ProjektValue value, final Object rel) {
        if (value.getBesitzer() == null) {
            throw new DataIntegrityViolationException("besitzer is null");
        }
        return Optional.empty();
    }

    @GetMapping("/projekt/search/findAllItem")
    public ResponseEntity<CollectionModel<ProjektItem>> findAllItem() {
        final List<ProjektItem> allItem = new ArrayList<>();
        for (final ProjektValue value : projektValueRepository.findAllByOrderByNameAsc()) {
            allItem.add(ProjektItem.fromValue(value));
        }
        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(allItem));
    }
}
