package esy.app.team;

import esy.api.team.Nutzer;
import esy.rest.JsonJpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "nutzer", collectionResourceRel = "allNutzer")
public interface NutzerRepository extends JsonJpaRepository<Nutzer> {

    /**
     * Returns all persisted values ordered by {@code mail} column.
     *
     * @return persisted values
     */
    List<Nutzer> findAllByOrderByMailAsc();

    /**
     * Returns a persisted value with given unique e-mail address
     * or nothing, if no value exists.
     *
     * @param mail unique e-mail address
     * @return persisted value or nothing
     */
    Optional<Nutzer> findByMail(@Param("mail") String mail);
}
