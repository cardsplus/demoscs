package esy.app.plan;

import esy.api.plan.Projekt;
import esy.rest.JsonJpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "projekt", collectionResourceRel = "allProjekt")
public interface ProjektRepository extends JsonJpaRepository<Projekt> {

    /**
     * Returns all persisted values ordered by {@code text} column.
     *
     * @return persisted values
     */
    List<Projekt> findAllByOrderByNameAsc();

    /**
     * Returns a persisted value with given unique name
     * or nothing, if no value exists.
     *
     * @param name unique name
     * @return persisted value or nothing
     */
    Optional<Projekt> findByName(@Param("name") String name);
}
