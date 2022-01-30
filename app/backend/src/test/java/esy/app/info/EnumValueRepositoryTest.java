package esy.app.info;

import esy.api.info.EnumValue;
import esy.app.DatabaseConfiguration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
@DataJpaTest
@ContextConfiguration(classes = DatabaseConfiguration.class)
@Transactional
@Rollback(false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EnumValueRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private EnumValueRepository enumValueRepository;

    @Test
    @Order(0)
    void context() {
        assertNotNull(dataSource);
        assertNotNull(jdbcTemplate);
        assertNotNull(entityManager);
        assertNotNull(transactionTemplate);
        assertNotNull(enumValueRepository);
    }

    static final String ENUM_ART = "TEST";

    EnumValue createWithName(final String name, final Long code) {
        final String json = "{" +
                "\"art\": \"" + ENUM_ART + "\"," +
                "\"code\": \"" + code + "\","+
                "\"name\": \"" + name + "\"," +
                "\"text\": \"A " + name + "\"" +
                "}";
        return EnumValue.parseJson(json);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "JIRA",
            "JIRA Cloud",
            "TEST",
            "Äpfel",
            "Öl",
            "Übel",
            "Spaß"
    })
    @Order(1)
    void saveEnum(final String name) {
        final Long code = enumValueRepository.count(ENUM_ART);
        final EnumValue value0 = createWithName(name, code);
        assertFalse(value0.isPersisted());
        assertEquals(0L, value0.getVersion());
        assertEquals(ENUM_ART, value0.getArt());
        assertEquals(code, value0.getCode());
        assertEquals(name, value0.getName());
        assertEquals("A " + name, value0.getText());

        final EnumValue value1 = transactionTemplate.execute(status ->
                enumValueRepository.save(value0));
        assertNotNull(value1);
        assertTrue(value1.isPersisted());
        assertEquals(0L, value1.getVersion());
        assertTrue(value1.isEqual(value0));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Test
    @Order(2)
    void saveEnumUniqueKeyConstraint() {
        assertEquals(7, enumValueRepository.count(ENUM_ART));
        assertThrows(DataIntegrityViolationException.class, () ->
                transactionTemplate.execute(status ->
                        enumValueRepository.save(createWithName("JIRA", 8L))));
        assertThrows(DataIntegrityViolationException.class, () ->
                transactionTemplate.execute(status ->
                        enumValueRepository.save(createWithName("ARIJ", 1L))));
    }

    @Test
    @Order(3)
    void findEnum() {
        assertEquals(7, enumValueRepository.count(ENUM_ART));
        final EnumValue value = enumValueRepository.findAll(ENUM_ART).get(0);
        assertEquals(value, enumValueRepository.findByCode(ENUM_ART, 0L).orElseThrow());
        assertEquals(value, enumValueRepository.findByName(ENUM_ART, "JIRA").orElseThrow());
        assertTrue(enumValueRepository.existsById(value.getId()));
        assertTrue(enumValueRepository.findById(value.getId()).orElseThrow().isEqual(value));
    }

    @Test
    @Order(4)
    void findEnumNoElement() {
        final UUID uuid = UUID.randomUUID();
        assertFalse(enumValueRepository.existsById(uuid));
        assertFalse(enumValueRepository.findById(uuid).isPresent());
    }

    @Test
    @Order(5)
    void findAll() {
        assertEquals(7, enumValueRepository.count(ENUM_ART));
        final List<EnumValue> allValue = enumValueRepository.findAll(ENUM_ART);
        assertEquals(7, allValue.size());
        assertEquals("JIRA", allValue.get(0).getName());
        assertEquals("JIRA Cloud", allValue.get(1).getName());
        assertEquals("TEST", allValue.get(2).getName());
        assertEquals("Äpfel", allValue.get(3).getName());
        assertEquals("Öl", allValue.get(4).getName());
        assertEquals("Übel", allValue.get(5).getName());
        assertEquals("Spaß", allValue.get(6).getName());
        assertTrue(allValue.removeAll(enumValueRepository.findAll()));
        assertTrue(allValue.isEmpty());
    }

    @Test
    @Order(99)
    void deleteAll() {
        assertEquals(7, enumValueRepository.count(ENUM_ART));
        enumValueRepository.findAll(ENUM_ART).forEach(e ->
                enumValueRepository.delete(e));
        assertEquals(0, enumValueRepository.count(ENUM_ART));
    }
}
