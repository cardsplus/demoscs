package esy.app.clinic;

import esy.api.clinic.Visit;
import esy.rest.JsonJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(path = "visit", collectionResourceRel = "allVisit")
public interface VisitRepository extends JsonJpaRepository<Visit> {

    /**
     * Returns all persisted entities.
     * Orders by {@code date} column.
     *
     * @return persisted entities
     */
    List<Visit> findAllByOrderByDateDesc();

    /**
     * Returns persisted entities for a {@link Owner}.
     * Orders by {@code date} column.
     *
     * @param ownerId a {@link Owner} id
     * @return persisted entities
     */
    @Query("SELECT v FROM Visit v " +
            "WHERE v.pet.owner.id = ?1 " +
            "ORDER BY v.date DESC")
    List<Visit> findAllByOwner(UUID ownerId);
}
