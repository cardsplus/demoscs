package scs.app.owner;

import esy.rest.JsonJpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import scs.api.owner.Pet;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "pet", collectionResourceRel = "allPet")
public interface PetRepository extends JsonJpaRepository<Pet> {

    /**
     * Returns all persisted entities ordered by {@code name} column.
     *
     * @return persisted entities
     */
    List<Pet> findAllByOrderByNameAsc();

    /**
     * Returns a persisted entity with given unique name
     * or nothing, if no value exists.
     *
     * @param name unique name
     * @return persisted entity or nothing
     */
    Optional<Pet> findByName(@Param("name") String name);
}
