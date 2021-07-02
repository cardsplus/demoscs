package esy.app.plan;

import esy.api.plan.AufgabeValue;
import esy.rest.JsonJpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "aufgabe", collectionResourceRel = "allAufgabe")
public interface AufgabeValueRepository extends JsonJpaRepository<AufgabeValue> {

    /**
     * Returns all persisted values ordered by {@code text} column.
     *
     * @return persisted values
     */
    List<AufgabeValue> findAllByOrderByTextAsc();
}
