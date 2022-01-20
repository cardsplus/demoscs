package esy.app.plan;

import esy.api.plan.AufgabeValue;
import esy.rest.JsonJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(path = "aufgabe", collectionResourceRel = "allAufgabe")
public interface AufgabeValueRepository extends JsonJpaRepository<AufgabeValue> {

    /**
     * Returns all persisted values ordered by {@code text} column.
     *
     * @return persisted values
     */
    List<AufgabeValue> findAllByOrderByTextAsc();
    
    /**
     * Returns persisted values for given criteria.
     * Orders by {@code termin} and {@code text} columns.
     *
     * @param projektId
     * @return persisted values
     */
    @Query("SELECT e FROM AufgabeValue e " +
            "WHERE e.projekt.id = ?1 " +
            "ORDER BY LOWER(e.text) ASC")
    List<AufgabeValue> findAllByProjekt(UUID projektId);
}
