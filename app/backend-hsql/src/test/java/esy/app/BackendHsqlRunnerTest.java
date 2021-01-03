package esy.app;

import esy.api.nutzer.NutzerRolle;
import esy.api.nutzer.NutzerValue;
import esy.api.plan.ProjektValue;
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
	void putApiNutzerRolle(final TestInfo info) throws Exception {
		final String name = info.getTestMethod().orElseThrow().getName();
		final String json = "{" +
				"\"mail\":\"" + name + "@a.de\"," +
				"\"name\":\"" + name + "\"," +
				"\"allRolle\": [%s]" +
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
		assertEquals(1, value0.getAllRolle().size());
		assertTrue(value0.getAllRolle().contains(NutzerRolle.BESUCHER));
		assertTrue(value0.isEqual(RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid)).get().toObject(NutzerValue.class)));

		for (final NutzerRolle rolle: NutzerRolle.values()) {
			final RestApiResult result1 = RestApiConnection.with(
					toBackendUrl("/api/nutzer/" + uuid))
					.put(String.format(json, "\"" + rolle.name() + "\""));
			assertThat(result1.getCode(),
					equalTo(HttpStatus.OK.value()));
			final NutzerValue value1 = result1.toObject(NutzerValue.class);
			assertEquals(uuid, value1.getDataId());
			assertEquals(name, value1.getName());
			assertEquals(1, value0.getAllRolle().size());
			assertTrue(value1.getAllRolle().contains(rolle));
			assertTrue(value1.isEqual(RestApiConnection.with(
					toBackendUrl("/api/nutzer/" + uuid)).get().toObject(NutzerValue.class)));
		}

		final RestApiResult result2 = RestApiConnection.with(
				toBackendUrl("/api/nutzer/" + uuid))
				.put(String.format(json, "\"BESUCHER\",\"BEARBEITER\",\"VERWALTER\""));
		assertThat(result2.getCode(),
				equalTo(HttpStatus.OK.value()));
		final NutzerValue value2 = result2.toObject(NutzerValue.class);
		assertEquals(uuid, value2.getDataId());
		assertEquals(name, value2.getName());
		assertEquals(3, value2.getAllRolle().size());
		assertTrue(value2.getAllRolle().contains(NutzerRolle.BESUCHER));
		assertTrue(value2.getAllRolle().contains(NutzerRolle.BEARBEITER));
		assertTrue(value2.getAllRolle().contains(NutzerRolle.VERWALTER));
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
		assertEquals(1, value3.getAllRolle().size());
		assertTrue(value3.getAllRolle().contains(NutzerRolle.BESUCHER));
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
						"\"besitzer\": {\"refId\": \"" + nutzer.getDataId() + "\"}," +
						"\"allMitglied\": [{\"refId\": \"" + nutzer.getDataId() + "\"}]" +
						"}");
		assertThat(result0.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final ProjektValue value0 = result0.toObject(ProjektValue.class);
		assertEquals(1L, value0.getVersion());
		assertNotNull(value0.getDataId());
		assertEquals(name, value0.getName());
		assertNotNull(value0.getBesitzer());
		assertFalse(value0.isAktiv());
		assertEquals(1, value0.getAllMitglied().size());

		final RestApiResult result1 = RestApiConnection.with(
				toBackendUrl("/api/projekt"))
				.post("{" +
						"\"name\":\"" + name + "\"," +
						"\"aktiv\": \"true\"," +
						"\"besitzer\": {\"refId\": \"" + nutzer.getDataId() + "\"}," +
						"\"allMitglied\": [{\"refId\": \"" + nutzer.getDataId() + "\"}]" +
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
				.filter(e -> e.getDataId().equals(value0.getDataId()))
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
		assertNull(value0.getBesitzer());
		assertEquals(0, value0.getAllMitglied().size());

		final RestApiResult result1 = RestApiConnection.with(
				toBackendUrl("/api/projekt/" + uuid))
				.put("{" +
						"\"name\":\"" + name + "\"," +
						"\"aktiv\": \"true\"," +
						"\"besitzer\": {\"refId\": \"" + nutzer.getDataId() + "\"}," +
						"\"allMitglied\": [{\"refId\": \"" + nutzer.getDataId() + "\"}]" +
						"}");
		assertThat(result1.getCode(),
				equalTo(HttpStatus.OK.value()));
		final ProjektValue value1 = result1.toObject(ProjektValue.class);
		assertEquals(1L, value1.getVersion());
		assertEquals(uuid, value1.getDataId());
		assertEquals(name, value1.getName());
		assertTrue(value1.isAktiv());
		assertNotNull(value1.getBesitzer());
		assertEquals(1, value1.getAllMitglied().size());
		assertFalse(value0.isEqual(value1));

		final RestApiResult result2a = RestApiConnection.with(
				toBackendUrl("/api/projekt?size=99"))
				.get();
		assertThat(result2a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<ProjektValue> allValue2a = result2a.toCollection(ProjektValue.class);
		assertEquals(1, allValue2a.size());
		assertEquals(1, allValue2a.stream()
				.filter(e -> e.getDataId().equals(uuid))
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
}
