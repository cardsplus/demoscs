package esy.app;

import esy.api.team.Sprache;
import esy.api.team.NutzerValue;
import esy.api.plan.AufgabeValue;
import esy.api.plan.ProjektValue;
import esy.http.RestApiConnection;
import esy.http.RestApiResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Tag("slow")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class BackendHsqlRunnerTest {

	@LocalServerPort
	private int port;

	String toBackendUrl(final String path) {
		return "http://localhost:" + port + path;
	}

	@Test
	@Order(1)
	void getHealth() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/actuator/health")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertThat(result.toString(),
				equalTo("{\"status\":\"UP\",\"groups\":[\"liveness\",\"readiness\"]}"));
	}

	@Test
	@Order(2)
	void getLiveness() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/actuator/health/liveness")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertThat(result.toString(),
				equalTo("{\"status\":\"UP\"}"));
	}

	@Test
	@Order(3)
	void getReadiness() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/actuator/health/readiness")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertThat(result.toString(),
				equalTo("{\"status\":\"UP\"}"));
	}

	@Test
	@Order(4)
	void getMappings() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/actuator/mappings")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
	}

	@Test
	@Order(5)
	void getMetrics() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/actuator/metrics")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
	}

	@Test
	@Order(6)
	void getLiquibase() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/actuator/liquibase")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
	}

	@Test
	@Order(10)
	void getVersion() throws Exception {
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
	@Order(110)
	void getApiNutzerNoElement() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/api/nutzer")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertEquals(0, result.toCollection(NutzerValue.class).size());
	}

	@Test
	@Order(111)
	void postApiNutzer() throws Exception {
		final String name = "Alf Mustermann";
		final String mail = name.replace(' ', '.') + "@a.de";

		final RestApiResult result0 = RestApiConnection.with(
				toBackendUrl("/api/nutzer"))
				.post("{" +
				"\"mail\":\"" + mail + "\"," +
				"\"name\":\"" + name + "\"," +
				"\"aktiv\": \"false\"," +
				"\"allRolle\": [\"BEARBEITER\"]" +
				"}");
		assertThat(result0.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final NutzerValue value0 = result0.toObject(NutzerValue.class);
		assertEquals(1L, value0.getVersion());
		assertNotNull(value0.getDataId());
		assertEquals(mail, value0.getMail());
		assertEquals(name, value0.getName());
		assertFalse(value0.isAktiv());

		final RestApiResult result1 = RestApiConnection.with(
				toBackendUrl("/api/nutzer"))
				.post("{" +
				"\"mail\":\"" + mail + "\"," +
				"\"name\":\"" + name + "\"" +
				"}");
		assertThat(result1.getCode(),
				equalTo(HttpStatus.CONFLICT.value()));

		final RestApiResult result2a = RestApiConnection.with(
				toBackendUrl("/api/nutzer?size=99"))
				.get();
		assertThat(result2a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<NutzerValue> allValue2a = result2a.toCollection(NutzerValue.class);
		assertEquals(1, allValue2a.size());
		assertEquals(1, allValue2a.stream()
				.filter(e -> e.getDataId().equals(value0.getDataId()))
				.count());

		final RestApiResult result2b = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + value0.getDataId()))
				.get();
		assertThat(result2b.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value0.isEqual(result2b.toObject(NutzerValue.class)));

		final RestApiResult result2c = RestApiConnection.with(
				toBackendUrl("/api/nutzer/search/findByMail?mail=" + URLEncoder.encode(mail, UTF_8)))
				.get();
		assertThat(result2c.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value0.isEqual(result2c.toObject(NutzerValue.class)));

		final RestApiResult result3a = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + value0.getDataId()))
				.delete();
		assertThat(result3a.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result3b = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + value0.getDataId()))
				.delete();
		assertThat(result3b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result4a = RestApiConnection.with(
				toBackendUrl("/api/nutzer?size=99"))
				.get();
		assertThat(result4a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<NutzerValue> allValue4a = result4a.toCollection(NutzerValue.class);
		assertEquals(0, allValue4a.size());

		final RestApiResult result4b = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + value0.getDataId()))
				.get();
		assertThat(result4b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result4c = RestApiConnection.with(
				toBackendUrl("/api/nutzer/search/findByMail?mail=" + URLEncoder.encode(mail, UTF_8)))
				.get();
		assertThat(result4c.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	@Order(112)
	void putApiNutzer() throws Exception {
		final String name = "Bea Musterfrau";
		final String mail = name.replace(' ', '.') + "@a.de";
		final UUID uuid = UUID.randomUUID();
		final RestApiResult result0 = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid))
				.put("{" +
				"\"mail\":\"" + mail + "\"," +
				"\"name\":\"" + name + "\"," +
				"\"aktiv\": \"false\"," +
				"\"allRolle\": [\"BEARBEITER\"]" +
				"}");
		assertThat(result0.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final NutzerValue value0 = result0.toObject(NutzerValue.class);
		assertEquals(1L, value0.getVersion());
		assertEquals(uuid, value0.getDataId());
		assertEquals(mail, value0.getMail());
		assertEquals(name, value0.getName());
		assertFalse(value0.isAktiv());

		final RestApiResult result1 = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid))
				.put("{" +
						"\"mail\":\"" + mail + "\"," +
						"\"name\":\"" + name + "\"," +
						"\"aktiv\": \"true\"," +
						"\"allRolle\": [\"BEARBEITER\"]" +
						"}");
		assertThat(result1.getCode(),
				equalTo(HttpStatus.OK.value()));
		final NutzerValue value1 = result1.toObject(NutzerValue.class);
		assertEquals(2L, value1.getVersion());
		assertEquals(uuid, value1.getDataId());
		assertEquals(mail, value1.getMail());
		assertEquals(name, value1.getName());
		assertTrue(value1.isAktiv());

		final RestApiResult result2a = RestApiConnection.with(
				toBackendUrl("/api/nutzer?size=99"))
				.get();
		assertThat(result2a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<NutzerValue> allValue2a = result2a.toCollection(NutzerValue.class);
		assertEquals(1, allValue2a.size());
		assertEquals(1, allValue2a.stream()
				.filter(e -> e.getDataId().equals(uuid))
				.count());

		final RestApiResult result2b = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid))
				.get();
		assertThat(result2b.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value1.isEqual(result2b.toObject(NutzerValue.class)));

		final RestApiResult result2c = RestApiConnection.with(
				toBackendUrl("/api/nutzer/search/findByMail?mail=" + URLEncoder.encode(mail, UTF_8)))
				.get();
		assertThat(result2c.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value1.isEqual(result2c.toObject(NutzerValue.class)));

		final RestApiResult result3a = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid))
				.delete();
		assertThat(result3a.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result3b = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid))
				.delete();
		assertThat(result3b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result4a = RestApiConnection.with(
				toBackendUrl("/api/nutzer?size=99"))
				.get();
		assertThat(result4a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<NutzerValue> allValue4a = result4a.toCollection(NutzerValue.class);
		assertEquals(0, allValue4a.size());

		final RestApiResult result4b = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid))
				.get();
		assertThat(result4b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result4c = RestApiConnection.with(
				toBackendUrl("/api/nutzer/search/findByMail?mail=" + URLEncoder.encode(mail, UTF_8)))
				.get();
		assertThat(result4c.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	@Order(113)
	void putApiNutzerSprache(final TestInfo info) throws Exception {
		final String name = info.getTestMethod().orElseThrow().getName();
		final String json = "{" +
				"\"mail\":\"" + name + "@a.de\"," +
				"\"name\":\"" + name + "\"," +
				"\"allSprache\": [%s]" +
				"}";
		final UUID uuid = UUID.randomUUID();

		final RestApiResult result0 = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid))
				.put(String.format(json, ""));
		assertThat(result0.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final NutzerValue value0 = result0.toObject(NutzerValue.class);
		assertEquals(uuid, value0.getDataId());
		assertEquals(name, value0.getName());
		assertEquals(1, value0.getAllSprache().size());
		assertTrue(value0.getAllSprache().contains(Sprache.DE));
		assertTrue(value0.isEqual(RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid)).get().toObject(NutzerValue.class)));

		for (final Sprache rolle: Sprache.values()) {
			final RestApiResult result1 = RestApiConnection.with(
					toBackendUrl("/api/nutzer/" + uuid))
					.put(String.format(json, "\"" + rolle.name() + "\""));
			assertThat(result1.getCode(),
					equalTo(HttpStatus.OK.value()));
			final NutzerValue value1 = result1.toObject(NutzerValue.class);
			assertEquals(uuid, value1.getDataId());
			assertEquals(name, value1.getName());
			assertEquals(1, value0.getAllSprache().size());
			assertTrue(value1.getAllSprache().contains(rolle));
			assertTrue(value1.isEqual(RestApiConnection.with(
					toBackendUrl("/api/nutzer/" + uuid)).get().toObject(NutzerValue.class)));
		}

		final RestApiResult result2 = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid))
				.put(String.format(json, "\"DE\",\"EN\",\"FR\",\"IT\""));
		assertThat(result2.getCode(),
				equalTo(HttpStatus.OK.value()));
		final NutzerValue value2 = result2.toObject(NutzerValue.class);
		assertEquals(uuid, value2.getDataId());
		assertEquals(name, value2.getName());
		assertEquals(4, value2.getAllSprache().size());
		assertTrue(value2.getAllSprache().contains(Sprache.DE));
		assertTrue(value2.getAllSprache().contains(Sprache.EN));
		assertTrue(value2.getAllSprache().contains(Sprache.FR));
		assertTrue(value2.getAllSprache().contains(Sprache.IT));
		assertTrue(value2.isEqual(RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid)).get().toObject(NutzerValue.class)));

		final RestApiResult result3 = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid))
				.put(String.format(json, ""));
		assertThat(result3.getCode(),
				equalTo(HttpStatus.OK.value()));
		final NutzerValue value3 = result3.toObject(NutzerValue.class);
		assertEquals(uuid, value3.getDataId());
		assertEquals(name, value3.getName());
		assertEquals(1, value3.getAllSprache().size());
		assertTrue(value3.getAllSprache().contains(Sprache.DE));
		assertTrue(value3.isEqual(RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid)).get().toObject(NutzerValue.class)));

		final RestApiResult result4 = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid))
				.put(String.format(json, "\"GARBAGE\""));
		assertThat(result4.getCode(),
				equalTo(HttpStatus.BAD_REQUEST.value()));
		assertTrue(value3.isEqual(RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid)).get().toObject(NutzerValue.class)));

		final RestApiResult result5 = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid))
				.delete();
		assertThat(result5.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));
	}

	@Test
	@Order(120)
	void getApiProjektNoElement() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/api/projekt")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertEquals(0, result.toCollection(ProjektValue.class).size());
	}

	@Test
	@Order(121)
	void postApiProjekt() throws Exception {
		final String name = "Projekt A";

		final NutzerValue nutzer = RestApiConnection.with(
				toBackendUrl("/api/nutzer"))
				.post("{" +
						"\"mail\":\"nutzer.a@a.de\"," +
						"\"name\":\"Nutzer A.\"," +
						"\"aktiv\": \"true\"" +
						"}")
				.toObject(NutzerValue.class);

		final RestApiResult result0 = RestApiConnection.with(
				toBackendUrl("/api/projekt"))
				.post("{" +
						"\"name\":\"" + name + "\"," +
						"\"aktiv\": \"false\"," +
						"\"besitzer\": \"/api/nutzer/" + nutzer.getDataId() + "\"," +
						"\"allMitglied\": [\"/api/nutzer/" + nutzer.getDataId() + "\"]" +
						"}");
		assertThat(result0.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final ProjektValue value0 = result0.toObject(ProjektValue.class);
		assertEquals(1L, value0.getVersion());
		assertNotNull(value0.getDataId());
		assertEquals(name, value0.getName());
		assertFalse(value0.isAktiv());

		final RestApiResult result1 = RestApiConnection.with(
				toBackendUrl("/api/projekt"))
				.post("{" +
						"\"name\":\"" + name + "\"" +
						"}");
		assertThat(result1.getCode(),
				equalTo(HttpStatus.CONFLICT.value()));

		final RestApiResult result2a = RestApiConnection.with(
				toBackendUrl("/api/projekt?size=99"))
				.get();
		assertThat(result2a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<ProjektValue> allValue2a = result2a.toCollection(ProjektValue.class);
		assertEquals(1, allValue2a.size());
		assertEquals(1, allValue2a.stream()
				.filter(e -> value0.getDataId().equals(e.getDataId()))
				.count());

		final RestApiResult result2b = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + value0.getDataId()))
				.get();
		assertThat(result2b.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value0.isEqual(result2b.toObject(ProjektValue.class)));

		final RestApiResult result2c = RestApiConnection.with(
				toBackendUrl("/api/projekt/search/findByName?name=" + URLEncoder.encode(name, UTF_8)))
				.get();
		assertThat(result2c.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value0.isEqual(result2c.toObject(ProjektValue.class)));

		final RestApiResult result2d = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + value0.getDataId() + "/besitzer"))
				.get();
		assertThat(result2d.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(nutzer.isEqual(result2d.toObject(NutzerValue.class)));

		final RestApiResult result2e = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + value0.getDataId()) + "/allMitglied")
				.get();
		assertThat(result2e.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<NutzerValue> allValue2e = result2e.toCollection(NutzerValue.class);
		assertEquals(1, allValue2e.size());
		assertEquals(1, allValue2e.stream()
				.filter(e -> nutzer.getDataId().equals(e.getDataId()))
				.count());

		final RestApiResult result3a = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + value0.getDataId()))
				.delete();
		assertThat(result3a.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result3b = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + value0.getDataId()))
				.delete();
		assertThat(result3b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result4a = RestApiConnection.with(
				toBackendUrl("/api/projekt?size=99"))
				.get();
		assertThat(result4a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<ProjektValue> allValue4a = result4a.toCollection(ProjektValue.class);
		assertEquals(0, allValue4a.size());

		final RestApiResult result4b = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + value0.getDataId()))
				.get();
		assertThat(result4b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result4c = RestApiConnection.with(
				toBackendUrl("/api/projekt/search/findByName?name=" + URLEncoder.encode(name, UTF_8)))
				.get();
		assertThat(result4c.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	@Order(122)
	void putApiProjekt() throws Exception {
		final String name = "Projekt B";

		final NutzerValue nutzer = RestApiConnection.with(
				toBackendUrl("/api/nutzer"))
				.post("{" +
						"\"mail\":\"nutzer.b@b.de\"," +
						"\"name\":\"Nutzer B.\"," +
						"\"aktiv\": \"true\"" +
						"}")
				.toObject(NutzerValue.class);

		final UUID uuid = UUID.randomUUID();
		final RestApiResult result0 = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + uuid))
				.put("{" +
						"\"name\":\"" + name + "\"," +
						"\"aktiv\": \"false\"" +
						"}");
		assertThat(result0.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final ProjektValue value0 = result0.toObject(ProjektValue.class);
		assertEquals(0L, value0.getVersion());
		assertEquals(uuid, value0.getDataId());
		assertEquals(name, value0.getName());
		assertFalse(value0.isAktiv());

		final RestApiResult result1a = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + uuid))
				.put("{" +
						"\"name\":\"" + name + "\"," +
						"\"aktiv\": \"true\"" +
						"}");
		assertThat(result1a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final ProjektValue value1 = result1a.toObject(ProjektValue.class);
		assertEquals(1L, value1.getVersion());
		assertTrue(value1.isAktiv());

		final RestApiResult result1b = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + uuid + "/besitzer"))
				.put(URI.create("/api/nutzer/" + nutzer.getDataId()));
		assertThat(result1b.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result1c = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + uuid + "/allMitglied"))
				.put(URI.create("/api/nutzer/" + nutzer.getDataId()));
		assertThat(result1c.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result2a = RestApiConnection.with(
				toBackendUrl("/api/projekt?size=99"))
				.get();
		assertThat(result2a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<ProjektValue> allValue2a = result2a.toCollection(ProjektValue.class);
		assertEquals(1, allValue2a.size());
		assertEquals(1, allValue2a.stream()
				.filter(e -> uuid.equals(e.getDataId()))
				.count());

		final RestApiResult result2b = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + uuid))
				.get();
		assertThat(result2b.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value1.isEqual(result2b.toObject(ProjektValue.class)));

		final RestApiResult result2c = RestApiConnection.with(
				toBackendUrl("/api/projekt/search/findByName?name=" + URLEncoder.encode(name, UTF_8)))
				.get();
		assertThat(result2c.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value1.isEqual(result2c.toObject(ProjektValue.class)));

		final RestApiResult result2d = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + uuid + "/besitzer"))
				.get();
		assertThat(result2d.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(nutzer.isEqual(result2d.toObject(NutzerValue.class)));

		final RestApiResult result2e = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + uuid + "/allMitglied"))
				.get();
		assertThat(result2e.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<NutzerValue> allValue2e = result2e.toCollection(NutzerValue.class);
		assertEquals(1, allValue2e.size());
		assertEquals(1, allValue2e.stream()
				.filter(e -> nutzer.getDataId().equals(e.getDataId()))
				.count());

		final RestApiResult result3a = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + uuid))
				.delete();
		assertThat(result3a.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result3b = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + uuid))
				.delete();
		assertThat(result3b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result4a = RestApiConnection.with(
				toBackendUrl("/api/projekt?size=99"))
				.get();
		assertThat(result4a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<ProjektValue> allValue4a = result4a.toCollection(ProjektValue.class);
		assertEquals(0, allValue4a.size());

		final RestApiResult result4b = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + uuid))
				.get();
		assertThat(result4b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result4c = RestApiConnection.with(
				toBackendUrl("/api/projekt/search/findByName?name=" + URLEncoder.encode(name, UTF_8)))
				.get();
		assertThat(result4c.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	@Order(130)
	void getApiAufgabeNoElement() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/api/aufgabe")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertEquals(0, result.toCollection(AufgabeValue.class).size());
	}

	@Test
	@Order(131)
	void postApiAufgabe() throws Exception {
		final String text = "Aufgabe A";

		final ProjektValue projekt = RestApiConnection.with(
				toBackendUrl("/api/projekt"))
				.post("{" +
						"\"name\":\"Projekt für " + text + "\"," +
						"\"aktiv\": \"false\"" +
						"}")
				.toObject(ProjektValue.class);

		final RestApiResult result0 = RestApiConnection.with(
				toBackendUrl("/api/aufgabe"))
				.post("{" +
						"\"text\":\"" + text + "\"," +
						"\"aktiv\": \"false\"," +
						"\"projekt\": \"/api/projekt/" + projekt.getDataId() + "\"" +
						"}");
		assertThat(result0.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final AufgabeValue value0 = result0.toObject(AufgabeValue.class);
		assertEquals(0L, value0.getVersion());
		assertNotNull(value0.getDataId());
		assertEquals(text, value0.getText());
		assertFalse(value0.isAktiv());
		assertNull(value0.getProjekt());

		final RestApiResult result1a = RestApiConnection.with(
				toBackendUrl("/api/aufgabe?size=99"))
				.get();
		assertThat(result1a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<AufgabeValue> allValue1a = result1a.toCollection(AufgabeValue.class);
		assertEquals(1, allValue1a.size());
		assertEquals(1, allValue1a.stream()
				.filter(e -> e.getDataId().equals(value0.getDataId()))
				.count());

		final RestApiResult result1b = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + value0.getDataId()))
				.get();
		assertThat(result1b.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value0.isEqual(result1b.toObject(AufgabeValue.class)));

		final RestApiResult result1c = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + value0.getDataId()) + "/projekt")
				.get();
		assertThat(result1c.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(projekt.isEqual(result1c.toObject(ProjektValue.class)));

		final RestApiResult result1d = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + projekt.getDataId() + "/allAufgabe"))
				.get();
		assertThat(result1d.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<AufgabeValue> allValue1d = result1d.toCollection(AufgabeValue.class);
		assertEquals(1, allValue1d.size());
		assertEquals(1, allValue1d.stream()
				.filter(e -> e.getDataId().equals(value0.getDataId()))
				.count());

		final RestApiResult result2 = RestApiConnection.with(
				toBackendUrl("/api/aufgabe"))
				.post("{" +
						"\"text\":\"" + text + "\"," +
						"\"aktiv\": \"true\"," +
						"\"projekt\": \"/api/projekt/" + projekt.getDataId() + "\"" +
						"}");
		assertThat(result2.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final AufgabeValue value2 = result2.toObject(AufgabeValue.class);
		assertEquals(0L, value2.getVersion());
		assertNotNull(value2.getDataId());
		assertEquals(text, value2.getText());
		assertTrue(value2.isAktiv());
		assertNull(value2.getProjekt());

		final RestApiResult result3a = RestApiConnection.with(
				toBackendUrl("/api/aufgabe?size=99"))
				.get();
		assertThat(result3a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<AufgabeValue> allValue3a = result3a.toCollection(AufgabeValue.class);
		assertEquals(2, allValue3a.size());
		assertEquals(1, allValue3a.stream()
				.filter(e -> e.getDataId().equals(value0.getDataId()))
				.count());
		assertEquals(1, allValue3a.stream()
				.filter(e -> e.getDataId().equals(value2.getDataId()))
				.count());

		final RestApiResult result3b = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + value2.getDataId()))
				.get();
		assertThat(result3b.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value2.isEqual(result3b.toObject(AufgabeValue.class)));

		final RestApiResult result3c = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + value2.getDataId()) + "/projekt")
				.get();
		assertThat(result3c.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(projekt.isEqual(result3c.toObject(ProjektValue.class)));

		final RestApiResult result3d = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + projekt.getDataId() + "/allAufgabe"))
				.get();
		assertThat(result3d.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<AufgabeValue> allValue3d = result3d.toCollection(AufgabeValue.class);
		assertEquals(2, allValue3d.size());
		assertEquals(1, allValue3d.stream()
				.filter(e -> e.getDataId().equals(value0.getDataId()))
				.count());
		assertEquals(1, allValue3d.stream()
				.filter(e -> e.getDataId().equals(value2.getDataId()))
				.count());

		final RestApiResult result4a = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + value0.getDataId()))
				.delete();
		assertThat(result4a.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result4b = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + value0.getDataId()))
				.delete();
		assertThat(result4b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result5a = RestApiConnection.with(
				toBackendUrl("/api/aufgabe?size=99"))
				.get();
		assertThat(result5a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<AufgabeValue> allValue5a = result5a.toCollection(AufgabeValue.class);
		assertEquals(1, allValue5a.size());
		assertEquals(0, allValue5a.stream()
				.filter(e -> e.getDataId().equals(value0.getDataId()))
				.count());

		final RestApiResult result5b = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + value0.getDataId()))
				.get();
		assertThat(result5b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result5c = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + value0.getDataId()) + "/projekt")
				.get();
		assertThat(result5c.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result5d = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + projekt.getDataId() + "/allAufgabe"))
				.get();
		assertThat(result5d.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<AufgabeValue> allValue5d = result5d.toCollection(AufgabeValue.class);
		assertEquals(1, allValue5d.size());
		assertEquals(0, allValue5d.stream()
				.filter(e -> e.getDataId().equals(value0.getDataId()))
				.count());

		final RestApiResult result6a = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + projekt.getDataId()))
				.delete();
		assertThat(result6a.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result6b = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + projekt.getDataId()))
				.delete();
		assertThat(result6b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result7a = RestApiConnection.with(
				toBackendUrl("/api/aufgabe?size=99"))
				.get();
		assertThat(result7a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<AufgabeValue> allValue7a = result7a.toCollection(AufgabeValue.class);
		assertEquals(0, allValue7a.size());

		final RestApiResult result7b = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + value2.getDataId()))
				.get();
		assertThat(result7b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result7c = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + value2.getDataId()) + "/projekt")
				.get();
		assertThat(result7c.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result7d = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + projekt.getDataId() + "/allAufgabe"))
				.get();
		assertThat(result7d.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	@Order(132)
	void putApiAufgabe() throws Exception {
		final String text = "Aufgabe B";
		final UUID uuid = UUID.randomUUID();

		final ProjektValue projekt = RestApiConnection.with(
				toBackendUrl("/api/projekt"))
				.post("{" +
						"\"name\":\"Projekt für " + text + "\"," +
						"\"aktiv\": \"false\"" +
						"}").toObject(ProjektValue.class);

		final RestApiResult result0 = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid))
				.put("{" +
						"\"text\":\"" + text + "\"," +
						"\"aktiv\": \"false\"," +
						"\"projekt\": \"/api/projekt/" + projekt.getDataId() + "\"" +
						"}");
		assertThat(result0.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final AufgabeValue value0 = result0.toObject(AufgabeValue.class);
		assertEquals(0L, value0.getVersion());
		assertEquals(uuid, value0.getDataId());
		assertEquals(text, value0.getText());
		assertFalse(value0.isAktiv());
		assertNull(value0.getProjekt());

		final RestApiResult result1a = RestApiConnection.with(
				toBackendUrl("/api/aufgabe?size=99"))
				.get();
		assertThat(result1a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<AufgabeValue> allValue1a = result1a.toCollection(AufgabeValue.class);
		assertEquals(1, allValue1a.size());
		assertEquals(1, allValue1a.stream()
				.filter(e -> uuid.equals(e.getDataId()))
				.count());

		final RestApiResult result1b = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid))
				.get();
		assertThat(result1b.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value0.isEqual(result1b.toObject(AufgabeValue.class)));

		final RestApiResult result1c = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid) + "/projekt")
				.get();
		assertThat(result1c.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(projekt.isEqual(result1c.toObject(ProjektValue.class)));

		final RestApiResult result1d = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + projekt.getDataId() + "/allAufgabe"))
				.get();
		assertThat(result1d.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<AufgabeValue> allValue1d = result1d.toCollection(AufgabeValue.class);
		assertEquals(1, allValue1d.size());
		assertEquals(1, allValue1d.stream()
				.filter(e -> uuid.equals(e.getDataId()))
				.count());

		final RestApiResult result2 = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid))
				.put("{" +
						"\"text\":\"" + text + "\"," +
						"\"aktiv\": \"true\"," +
						"\"projekt\": \"/api/projekt/" + projekt.getDataId() + "\"" +
						"}");
		assertThat(result2.getCode(),
				equalTo(HttpStatus.OK.value()));
		final AufgabeValue value2 = result2.toObject(AufgabeValue.class);
		assertEquals(1L, value2.getVersion());
		assertEquals(uuid, value2.getDataId());
		assertEquals(text, value2.getText());
		assertTrue(value2.isAktiv());
		assertNull(value2.getProjekt());

		final RestApiResult result3a = RestApiConnection.with(
				toBackendUrl("/api/aufgabe?size=99"))
				.get();
		assertThat(result3a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<AufgabeValue> allValue3a = result3a.toCollection(AufgabeValue.class);
		assertEquals(1, allValue3a.size());
		assertEquals(1, allValue3a.stream()
				.filter(e -> uuid.equals(e.getDataId()))
				.count());

		final RestApiResult result3b = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid))
				.get();
		assertThat(result3b.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value2.isEqual(result3b.toObject(AufgabeValue.class)));

		final RestApiResult result3c = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid + "/projekt"))
				.get();
		assertThat(result3c.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(projekt.isEqual(result3c.toObject(ProjektValue.class)));

		final RestApiResult result3d = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + projekt.getDataId() + "/allAufgabe"))
				.get();
		assertThat(result3d.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<AufgabeValue> allValue3d = result3d.toCollection(AufgabeValue.class);
		assertEquals(1, allValue3d.size());
		assertEquals(1, allValue3d.stream()
				.filter(e -> e.getDataId().equals(uuid))
				.count());

		final RestApiResult result4a = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid))
				.delete();
		assertThat(result4a.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result4b = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid))
				.delete();
		assertThat(result4b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result4c = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + projekt.getDataId()))
				.delete();
		assertThat(result4c.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result5a = RestApiConnection.with(
				toBackendUrl("/api/aufgabe?size=99"))
				.get();
		assertThat(result5a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<AufgabeValue> allValue5a = result5a.toCollection(AufgabeValue.class);
		assertEquals(0, allValue5a.size());

		final RestApiResult result5b = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid))
				.get();
		assertThat(result5b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result5c = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid) + "/projekt")
				.get();
		assertThat(result5c.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));

		final RestApiResult result5d = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + projekt.getDataId() + "/allAufgabe"))
				.get();
		assertThat(result5d.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	@Order(133)
	void putApiAufgabeProjekt() throws Exception {
		final String text = "Aufgabe B";
		final UUID uuid = UUID.randomUUID();

		final ProjektValue projekt1 = RestApiConnection.with(
				toBackendUrl("/api/projekt"))
				.post("{" +
						"\"name\":\"Projekt #1 für " + text + "\"," +
						"\"aktiv\": \"false\"" +
						"}").toObject(ProjektValue.class);

		final ProjektValue projekt2 = RestApiConnection.with(
				toBackendUrl("/api/projekt"))
				.post("{" +
						"\"name\":\"Projekt #2 für " + text + "\"," +
						"\"aktiv\": \"false\"" +
						"}").toObject(ProjektValue.class);

		final RestApiResult result0 = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid))
				.put("{" +
						"\"text\":\"" + text + "\"," +
						"\"aktiv\": \"false\"," +
						"\"projekt\": \"/api/projekt/" + projekt1.getDataId() + "\"" +
						"}");
		assertThat(result0.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final AufgabeValue value0 = result0.toObject(AufgabeValue.class);
		assertEquals(0L, value0.getVersion());
		assertEquals(uuid, value0.getDataId());
		assertEquals(text, value0.getText());
		assertFalse(value0.isAktiv());
		assertNull(value0.getProjekt());

		final RestApiResult result1 = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid) + "/projekt")
				.get();
		assertThat(result1.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(projekt1.isEqual(result1.toObject(ProjektValue.class)));

		final RestApiResult result2 = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid + "/projekt"))
				.put(URI.create("/api/projekt/" + projekt2.getDataId()));
		assertThat(result2.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result3 = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid) + "/projekt")
				.get();
		assertThat(result3.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(projekt2.isEqual(result3.toObject(ProjektValue.class)));

		final RestApiResult result4 = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid + "/projekt"))
				.put(URI.create("/api/projekt/" + UUID.randomUUID()));
		assertThat(result4.getCode(),
				equalTo(HttpStatus.CONFLICT.value()));

		final RestApiResult result5 = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid + "/projekt"))
				.delete();
		assertThat(result5.getCode(),
				equalTo(HttpStatus.CONFLICT.value()));

		final RestApiResult result6a = RestApiConnection.with(
				toBackendUrl("/api/aufgabe/" + uuid))
				.delete();
		assertThat(result6a.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result6b = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + projekt1.getDataId()))
				.delete();
		assertThat(result6b.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result6c = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + projekt2.getDataId()))
				.delete();
		assertThat(result6c.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));
	}
}
