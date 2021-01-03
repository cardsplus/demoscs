package esy.app.plan;

import esy.api.plan.AufgabeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(path = "aufgabe", collectionResourceRel = "allAufgabe")
public interface AufgabeValueRepository extends JpaRepository<AufgabeValue, UUID> {

    /**
     * Returns all persisted values ordered by {@code text} column.
     *
     * @return persisted values
     */
    List<AufgabeValue> findAllByOrderByTextAsc();
}
