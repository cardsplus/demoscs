package scs.app.clinic;

import esy.rest.JsonJpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import scs.api.clinic.Vet;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "vet", collectionResourceRel = "allVet")
public interface VetRepository extends JsonJpaRepository<Vet> {

    /**
     * Returns all persisted entities.
     * Orders by {@code name} column.
     *
     * @return persisted entities
     */
    List<Vet> findAllByOrderByNameAsc();

    /**
     * Returns a persisted entity with given unique name
     * or nothing, if no value exists.
     *
     * @param name unique name
     * @return persisted entity or nothing
     */
    Optional<Vet> findByName(@Param("name") String name);
}
