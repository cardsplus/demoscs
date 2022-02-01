package scs.app.clinic;

import esy.rest.JsonJpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import scs.api.clinic.Visit;

import java.util.List;

@RepositoryRestResource(path = "visit", collectionResourceRel = "allVisit")
public interface VisitRepository extends JsonJpaRepository<Visit> {

    /**
     * Returns all persisted entities ordered by {@code date} column.
     *
     * @return persisted entities
     */
    List<Visit> findAllByOrderByDateAsc();
}
