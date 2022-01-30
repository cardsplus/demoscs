package esy.app.info;

import esy.api.info.EnumValue;
import esy.rest.JsonJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "enum", collectionResourceRel = "allEnum")
public interface EnumValueRepository extends JsonJpaRepository<EnumValue> {

    /**
     * Returns all persisted values of given discriminator.
     *
     * @param art discriminator
     * @return persisted values
     */
    @Query("SELECT e FROM EnumValue e " +
            "WHERE e.art = ?1 " +
            "ORDER BY e.art ASC, e.code")
    List<EnumValue> findAll(String art);

    /**
     * Returns all persisted values of given discriminator
     * for a given unique code.
     * Orders by {@code name} column.
     *
     * @param art discriminator
     * @param code unique code
     * @return persisted values
     */
    @Query("SELECT e FROM EnumValue e " +
            "WHERE e.art = ?1 " +
            "AND e.code = ?2 " +
            "ORDER BY e.art ASC, e.code")
    Optional<EnumValue> findByCode(String art, Long code);

    /**
     * Returns all persisted values of given discriminator
     * for a given unique name.
     * Orders by {@code name} column.
     *
     * @param art discriminator
     * @param name unique name
     * @return persisted values
     */
    @Query("SELECT e FROM EnumValue e " +
            "WHERE e.art = ?1 " +
            "AND e.name = ?2 " +
            "ORDER BY e.art ASC, e.code")
    Optional<EnumValue> findByName(String art, String name);

    /**
     * Returns the number of persisted values of given discriminator.
     *
     * @param art discriminator
     * @return the number of values
     */
    @Query("SELECT COUNT(e) FROM EnumValue e " +
            "WHERE e.art = ?1")
    long count(String art);
}
