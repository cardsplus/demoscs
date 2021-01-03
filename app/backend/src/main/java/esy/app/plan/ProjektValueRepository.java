package esy.app.plan;

import esy.api.plan.ProjektValue;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(path = "projekt", collectionResourceRel = "allProjekt")
public interface ProjektValueRepository extends JpaRepository<ProjektValue, UUID> {

    /**
     * Returns all persisted values ordered by {@code text} column.
     *
     * @return persisted values
     */
    List<ProjektValue> findAllByOrderByNameAsc();

    /**
     * Returns a persisted value with given unique name
     * or nothing, if no value exists.
     *
     * @param name unique name
     * @return persisted value or nothing
     */
    Optional<ProjektValue> findByName(@NonNull final String name);
}
