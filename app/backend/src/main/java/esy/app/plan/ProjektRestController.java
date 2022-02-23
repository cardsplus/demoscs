package esy.app.plan;

import esy.api.plan.Projekt;
import esy.api.plan.ProjektItem;
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
public class ProjektRestController extends JsonJpaRestControllerBase<Projekt> {

    private final ProjektRepository projektRepository;

    @Autowired
    public ProjektRestController(
            @NonNull final ApplicationEventPublisher eventPublisher,
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final ProjektRepository projektRepository) {
        super(eventPublisher, transactionTemplate);
        this.projektRepository = projektRepository;
    }

    @Override
    protected Optional<TransactionCallback<Void>> beforeLinkDeleteTransaction(final Projekt value, final Object rel) {
        if (value.getBesitzer() == null) {
            throw new DataIntegrityViolationException("besitzer is null");
        }
        return Optional.empty();
    }

    @GetMapping("/projekt/search/findAllItem")
    public ResponseEntity<CollectionModel<ProjektItem>> findAllItem() {
        final List<ProjektItem> allItem = new ArrayList<>();
        for (final Projekt value : projektRepository.findAllByOrderByNameAsc()) {
            allItem.add(ProjektItem.fromValue(value));
        }
        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(allItem));
    }
}
