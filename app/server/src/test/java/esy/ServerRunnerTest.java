package esy;

import esy.api.plan.Aufgabe;
import esy.api.plan.Projekt;
import esy.api.team.Nutzer;
import esy.http.RestApiConnection;
import esy.http.RestApiResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Tag("slow")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class ServerRunnerTest {

    @LocalServerPort
    private int port;

    String toBackendUrl(final String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    @Order(1)
    void health() throws Exception {
        final RestApiResult result = RestApiConnection.with(
                toBackendUrl("/actuator/health")).get();
        assertThat(result.getCode(),
                equalTo(HttpStatus.OK.value()));
        assertThat(result.toString(),
                equalTo("{\"status\":\"UP\",\"groups\":[\"liveness\",\"readiness\"]}"));
    }

    @Test
    @Order(2)
    void version() throws Exception {
        final RestApiResult result = RestApiConnection.with(
                toBackendUrl("/version")).get();
        assertThat(result.getCode(),
                equalTo(HttpStatus.OK.value()));
        final Map<?,?> allValue = result.toObject(Map.class);
        assertEquals(2, allValue.size());
        assertTrue(allValue.containsKey("major"));
        assertTrue(allValue.containsKey("minor"));
    }

    @Test
    @Order(2)
    void apiEnumSprache() throws Exception {
        final RestApiResult result = RestApiConnection.with(
                toBackendUrl("/api/enum/sprache")).get();
        assertThat(result.getCode(),
                equalTo(HttpStatus.OK.value()));
        assertEquals(4, result.toCollection(Nutzer.class).size());
    }

    @Test
    @Order(10)
    void apiNutzer() throws Exception {
        final String name = "Alf Mustermann";
        final String mail = "alf.mustermann@firma.de";

        final RestApiResult result1a = RestApiConnection.with(
                toBackendUrl("/api/nutzer"))
                .post("{" +
                        "\"mail\":\"" + mail + "\"," +
                        "\"name\":\"" + name + "\"," +
                        "\"aktiv\": \"false\"," +
                        "\"allSprache\": [\"DE\"]" +
                        "}");
        assertThat(result1a.getCode(),
                equalTo(HttpStatus.CREATED.value()));
        final Nutzer value1 = result1a.toObject(Nutzer.class);
        assertEquals(1L, value1.getVersion());
        assertNotNull(value1.getId());
        assertEquals(mail, value1.getMail());
        assertEquals(name, value1.getName());
        assertFalse(value1.isAktiv());
        assertEquals(1, value1.getAllSprache().size());

        final RestApiResult result1b = RestApiConnection.with(
                toBackendUrl("/api/nutzer"))
                .post("{" +
                        "\"mail\":\"" + mail + "\"," +
                        "\"name\":\"" + name + "\"" +
                        "}");
        assertThat(result1b.getCode(),
                equalTo(HttpStatus.CONFLICT.value()));

        final RestApiResult result2a = RestApiConnection.with(
                toBackendUrl("/api/nutzer/" + value1.getId()))
                .put("{" +
                        "\"mail\":\"" + mail + "\"," +
                        "\"name\":\"" + name + "\"," +
                        "\"aktiv\": \"true\"," +
                        "\"allSprache\": [\"DE\",\"EN\"]" +
                        "}");
        assertThat(result2a.getCode(),
                equalTo(HttpStatus.OK.value()));
        final Nutzer value2 = result2a.toObject(Nutzer.class);
        assertFalse(value1.isEqual(result2a.toObject(Nutzer.class)));
        assertEquals(2L, value2.getVersion());
        assertNotNull(value2.getId());
        assertEquals(mail, value2.getMail());
        assertEquals(name, value2.getName());
        assertEquals(2, value2.getAllSprache().size());
        assertTrue(value2.isAktiv());

        final RestApiResult result3a = RestApiConnection.with(
                toBackendUrl("/api/nutzer/" + value2.getId()))
                .get();
        assertThat(result3a.getCode(),
                equalTo(HttpStatus.OK.value()));
        assertTrue(value2.isEqual(result3a.toObject(Nutzer.class)));

        final RestApiResult result3b = RestApiConnection.with(
                toBackendUrl("/api/nutzer/search/findByMail?mail=" + URLEncoder.encode(mail, UTF_8)))
                .get();
        assertThat(result3b.getCode(),
                equalTo(HttpStatus.OK.value()));
        assertTrue(value2.isEqual(result3b.toObject(Nutzer.class)));

        final RestApiResult result3c = RestApiConnection.with(
                toBackendUrl("/api/nutzer?size=99"))
                .get();
        assertThat(result3c.getCode(),
                equalTo(HttpStatus.OK.value()));
        final List<Nutzer> allValue2a = result3c.toCollection(Nutzer.class);
        assertEquals(8, allValue2a.size());
        assertEquals(1, allValue2a.stream()
                .filter(e -> e.getId().equals(value1.getId()))
                .count());

        final RestApiResult result4a = RestApiConnection.with(
                toBackendUrl("/api/nutzer/" + value1.getId()))
                .delete();
        assertThat(result4a.getCode(),
                equalTo(HttpStatus.NO_CONTENT.value()));

        final RestApiResult result4b = RestApiConnection.with(
                toBackendUrl("/api/nutzer/" + value1.getId()))
                .delete();
        assertThat(result4b.getCode(),
                equalTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @Order(20)
    void apiProjekt() throws Exception {
        final String name = "Projekt A";
        final String mail = "max.mustermann@firma.de";

        final Nutzer nutzer1 = RestApiConnection.with(
                        toBackendUrl("/api/nutzer/search/findByMail?mail=" + URLEncoder.encode(mail, UTF_8)))
                .get()
                .toObject(Nutzer.class);

        final RestApiResult result1a = RestApiConnection.with(
                toBackendUrl("/api/projekt"))
                .post("{" +
                        "\"name\":\"" + name + "\"," +
                        "\"aktiv\": \"false\"," +
                        "\"besitzer\": \"/api/nutzer/" + nutzer1.getId() + "\"," +
                        "\"allMitglied\": [\"/api/nutzer/" + nutzer1.getId() + "\"]" +
                        "}");
        assertThat(result1a.getCode(),
                equalTo(HttpStatus.CREATED.value()));
        final Projekt value1 = result1a.toObject(Projekt.class);
        assertEquals(1L, value1.getVersion());
        assertNotNull(value1.getId());
        assertEquals(name, value1.getName());
        assertFalse(value1.isAktiv());

        final RestApiResult result1b = RestApiConnection.with(
                toBackendUrl("/api/projekt"))
                .post("{" +
                        "\"name\":\"" + name + "\"" +
                        "}");
        assertThat(result1b.getCode(),
                equalTo(HttpStatus.CONFLICT.value()));

        final RestApiResult result2a = RestApiConnection.with(
                toBackendUrl("/api/projekt/" + value1.getId()))
                .put("{" +
                        "\"name\":\"" + name + "\"," +
                        "\"aktiv\": \"true\"" +
                        "}");
        assertThat(result2a.getCode(),
                equalTo(HttpStatus.OK.value()));
        final Projekt value2 = result2a.toObject(Projekt.class);
        assertEquals(2L, value2.getVersion());
        assertNotNull(value2.getId());
        assertEquals(name, value2.getName());
        assertTrue(value2.isAktiv());

        final RestApiResult result3a = RestApiConnection.with(
                toBackendUrl("/api/projekt?size=99"))
                .get();
        assertThat(result3a.getCode(),
                equalTo(HttpStatus.OK.value()));
        final List<Projekt> allValue2a = result3a.toCollection(Projekt.class);
        assertEquals(4, allValue2a.size());
        assertEquals(1, allValue2a.stream()
                .filter(e -> value2.getId().equals(e.getId()))
                .count());

        final RestApiResult result3b = RestApiConnection.with(
                toBackendUrl("/api/projekt/" + value2.getId()))
                .get();
        assertThat(result3b.getCode(),
                equalTo(HttpStatus.OK.value()));
        assertTrue(value2.isEqual(result3b.toObject(Projekt.class)));

        final RestApiResult result3c = RestApiConnection.with(
                toBackendUrl("/api/projekt/search/findByName?name=" + URLEncoder.encode(name, UTF_8)))
                .get();
        assertThat(result3c.getCode(),
                equalTo(HttpStatus.OK.value()));
        assertTrue(value2.isEqual(result3c.toObject(Projekt.class)));

        final RestApiResult result3d = RestApiConnection.with(
                toBackendUrl("/api/projekt/" + value2.getId() + "/besitzer"))
                .get();
        assertThat(result3d.getCode(),
                equalTo(HttpStatus.OK.value()));
        assertTrue(nutzer1.isEqual(result3d.toObject(Nutzer.class)));

        final RestApiResult result3e = RestApiConnection.with(
                toBackendUrl("/api/projekt/" + value2.getId()) + "/allMitglied")
                .get();
        assertThat(result3e.getCode(),
                equalTo(HttpStatus.OK.value()));
        final List<Nutzer> allValue2e = result3e.toCollection(Nutzer.class);
        assertEquals(1, allValue2e.size());
        assertEquals(1, allValue2e.stream()
                .filter(e -> nutzer1.getId().equals(e.getId()))
                .count());

        final RestApiResult result4a = RestApiConnection.with(
                toBackendUrl("/api/projekt/" + value2.getId()))
                .delete();
        assertThat(result4a.getCode(),
                equalTo(HttpStatus.NO_CONTENT.value()));

        final RestApiResult result4b = RestApiConnection.with(
                toBackendUrl("/api/projekt/" + value2.getId()))
                .delete();
        assertThat(result4b.getCode(),
                equalTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @Order(30)
    void apiAufgabe() throws Exception {
        final String name = "Projekt Gamma";
        final String text = "Aufgabe A";

        final Projekt projekt = RestApiConnection.with(
                toBackendUrl("/api/projekt/search/findByName?name=" + URLEncoder.encode(name, UTF_8)))
                .get()
                .toObject(Projekt.class);

        final RestApiResult result1a = RestApiConnection.with(
                toBackendUrl("/api/aufgabe"))
                .post("{" +
                        "\"text\":\"" + text + "\"," +
                        "\"aktiv\": \"false\"," +
                        "\"projekt\": \"/api/projekt/" + projekt.getId() + "\"" +
                        "}");
        assertThat(result1a.getCode(),
                equalTo(HttpStatus.CREATED.value()));
        final Aufgabe value1 = result1a.toObject(Aufgabe.class);
        assertEquals(0L, value1.getVersion());
        assertNotNull(value1.getId());
        assertEquals(text, value1.getText());
        assertFalse(value1.isAktiv());
        assertNull(value1.getProjekt());

        final RestApiResult result1b = RestApiConnection.with(
                toBackendUrl("/api/aufgabe/search/findAllByProjekt?projektId=" + projekt.getId()))
                .get();
        assertThat(result1b.getCode(),
                equalTo(HttpStatus.OK.value()));
        final List<Aufgabe> allValue1 = result1b.toCollection(Aufgabe.class);
        assertEquals(1, allValue1.size());
        assertEquals(1, allValue1.stream()
                .filter(e -> e.getId().equals(value1.getId()))
                .count());

        final RestApiResult result2a = RestApiConnection.with(
                        toBackendUrl("/api/aufgabe"))
                .post("{" +
                        "\"text\":\"" + text + "\"," +
                        "\"aktiv\": \"false\"," +
                        "\"projekt\": \"/api/projekt/" + projekt.getId() + "\"" +
                        "}");
        assertThat(result2a.getCode(),
                equalTo(HttpStatus.CREATED.value()));
        final Aufgabe value2 = result2a.toObject(Aufgabe.class);
        assertEquals(0L, value2.getVersion());
        assertNotNull(value2.getId());
        assertEquals(text, value2.getText());
        assertFalse(value2.isAktiv());
        assertNull(value2.getProjekt());

        final RestApiResult result2b = RestApiConnection.with(
                toBackendUrl("/api/aufgabe/search/findAllByProjekt?projektId=" + projekt.getId()))
                .get();
        assertThat(result2b.getCode(),
                equalTo(HttpStatus.OK.value()));
        final List<Aufgabe> allValue2 = result2b.toCollection(Aufgabe.class);
        assertEquals(2, allValue2.size());
        assertEquals(1, allValue2.stream()
                .filter(e -> e.getId().equals(value2.getId()))
                .count());

        final RestApiResult result3a = RestApiConnection.with(
                toBackendUrl("/api/aufgabe/" + value1.getId()))
                .delete();
        assertThat(result3a.getCode(),
                equalTo(HttpStatus.NO_CONTENT.value()));

        final RestApiResult result3b = RestApiConnection.with(
                toBackendUrl("/api/aufgabe/" + value1.getId()))
                .delete();
        assertThat(result3b.getCode(),
                equalTo(HttpStatus.NOT_FOUND.value()));

        final RestApiResult result4a = RestApiConnection.with(
                toBackendUrl("/api/aufgabe/" + value2.getId()))
                .delete();
        assertThat(result4a.getCode(),
                equalTo(HttpStatus.NO_CONTENT.value()));

        final RestApiResult result4b = RestApiConnection.with(
                toBackendUrl("/api/aufgabe/" + value2.getId()))
                .delete();
        assertThat(result4b.getCode(),
                equalTo(HttpStatus.NOT_FOUND.value()));
    }
}
