package esy.app.team;

import esy.api.team.NutzerValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(path = "nutzer", collectionResourceRel = "allNutzer")
public interface NutzerValueRepository extends JpaRepository<NutzerValue, UUID> {

    /**
     * Returns all persisted values ordered by {@code mail} column.
     *
     * @return persisted values
     */
    List<NutzerValue> findAllByOrderByMailAsc();

    /**
     * Returns a persisted value with given unique e-mail address
     * or nothing, if no value exists.
     *
     * @param mail unique e-mail address
     * @return persisted value or nothing
     */
    Optional<NutzerValue> findByMail(@Param("mail") String mail);
}
