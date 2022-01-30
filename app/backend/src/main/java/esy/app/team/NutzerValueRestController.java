package esy.app.team;

import esy.api.team.NutzerItem;
import esy.api.team.NutzerValue;
import esy.rest.JsonJpaRestControllerBase;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@RepositoryEventHandler
@BasePathAwareController
public class NutzerValueRestController extends JsonJpaRestControllerBase<NutzerValue> {

    private final NutzerValueRepository nutzerValueRepository;

    @Autowired
    public NutzerValueRestController(
            @NonNull final ApplicationEventPublisher eventPublisher,
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final NutzerValueRepository nutzerValueRepository) {
        super(eventPublisher, transactionTemplate);
        this.nutzerValueRepository = nutzerValueRepository;
    }

    @GetMapping("/nutzer/search/findAllItem")
    public ResponseEntity<CollectionModel<NutzerItem>> findAllItem() {
        final List<NutzerItem> allItem = new ArrayList<>();
        for (final NutzerValue value : nutzerValueRepository.findAllByOrderByMailAsc()) {
            allItem.add(NutzerItem.fromValue(value));
        }
        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(allItem));
    }
}
