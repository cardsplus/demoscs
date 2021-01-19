package esy.api.plan;

import esy.api.team.NutzerValue;
import esy.api.team.Sprache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ProjektValueTest {

	static ProjektValue createWithName(final String name) {
		final UUID uuid = UUID.randomUUID();
		final String json = "{" +
				"\"version\": \"1\"," +
				"\"dataId\": \"" + uuid + "\"," +
				"\"name\": \"" + name + "\"," +
				"\"aktiv\": \"false\"," +
				"\"sprache\": \"EN\"," +
				"\"besitzer\": null," +
				"\"allMitglied\": [" +
				"]" +
				"}";
		return ProjektValue.parseJson(json);
	}

	@Test
	void equalsHashcodeToString() {
		final String name = "Projekt A";
		final ProjektValue value = createWithName(name);
		// Identisches Objekt
		assertEquals(value, value);
		assertTrue(value.isEqual(value));
		assertEquals(value.hashCode(), value.hashCode());
		assertEquals(value.toString(), value.toString());
		// Gleiches Objekt
		final ProjektValue clone = ProjektValue.parseJson(value.writeJson());
		assertTrue(clone.isEqual(value));
		assertEquals(clone.hashCode(), value.hashCode());
		assertEquals(clone.toString(), value.toString());
		// Gleicher Text
		assertNotEquals(createWithName(name), value);
		assertTrue(value.isEqual(createWithName(name)));
		assertNotEquals(createWithName(name).hashCode(), value.hashCode());
		assertNotEquals(createWithName(name).toString(), value.toString());
		// Anderer Text
		assertNotEquals(createWithName("X" + name), value);
		assertFalse(value.isEqual(createWithName("X" + name)));
		assertNotEquals(createWithName("X" + name).hashCode(), value.hashCode());
		assertNotEquals(createWithName("X" + name).toString(), value.toString());
		// Kein Objekt
		assertNotEquals(value, null);
		assertFalse(value.isEqual(null));
		// Falsches Objekt
		assertNotEquals(this, value);
	}

	@Test
	void withDataId() {
		final String name = "Projekt A";
		final ProjektValue value0 = createWithName(name);
		final ProjektValue value1 = value0.withDataId(value0.getDataId());
		assertSame(value0, value1);
		final ProjektValue value2 = value0.withDataId(UUID.randomUUID());
		assertNotSame(value0, value2);
		assertTrue(value0.isEqual(value2));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"metaId",
			"metaCreated",
			"garbage"
	})
	void jsonGarbage(final String key) {
		final String name = "Projekt A";
		final UUID uuid = UUID.randomUUID();
		final String json = "{" +
				"\"version\": \"1\"," +
				"\"dataId\": \"" + uuid + "\"," +
				"\"name\": \"" + name + "\"," +
				"\"" + key + "\": \"" + name + "\"" +
				"}";
		final ProjektValue value = ProjektValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(1L, value.getVersion());
		assertNotNull(value.getDataId());
		assertEquals(name, value.getName());
		assertTrue(value.isAktiv());
		assertNull(value.getBesitzer());
		assertEquals(0, value.getAllMitglied().size());
	}

	@Test
	void json() {
		final String name = "Projekt A";
		final ProjektValue value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertEquals(1L, value.getVersion());
		assertNotNull(value.getDataId());
		assertEquals(name, value.getName());
		assertFalse(value.isAktiv());
		assertEquals(Sprache.EN, value.getSprache());
		assertNull(value.getBesitzer());
		assertEquals(0, value.getAllMitglied().size());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{}",
			"{\"name\": \"\"}",
			"{\"aktiv\": \"false\"}"
	})
	void jsonNameConstraints(final String json) {
		final ProjektValue value = ProjektValue.parseJson(json);
		assertThrows(IllegalArgumentException.class, value::verify);
		assertTrue(value.getName().isBlank());
	}

	@Test
	public void jsonName() {
		final String name = "Projekt A";
		final String json = "{" +
				"\"name\": \"" + name + "\"" +
				"}";
		final ProjektValue value = ProjektValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(name, value.getName());

		assertThrows(NullPointerException.class, () -> value.setName(null));

		value.setName("X" + name);
		assertFalse(value.isEqual(ProjektValue.parseJson(json)));

		value.setName(name);
		assertTrue(value.isEqual(ProjektValue.parseJson(json)));
	}

	@Test
	public void jsonAktiv() {
		final String name = "Projekt A";
		final String json = "{" +
				"\"name\": \"" + name + "\"," +
				"\"aktiv\": \"false\"" +
				"}";
		final ProjektValue value = ProjektValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(name, value.getName());
		assertFalse(value.isAktiv());

		value.setAktiv(true);
		assertFalse(value.isEqual(ProjektValue.parseJson(json)));

		value.setAktiv(false);
		assertTrue(value.isEqual(ProjektValue.parseJson(json)));
	}

	@Test
	public void jsonSprache() {
		final String name = "Projekt A";
		final String json = "{" +
				"\"name\": \"" + name + "\"," +
				"\"sprache\": \"EN\"" +
				"}";
		final ProjektValue value = ProjektValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(name, value.getName());
		assertEquals(Sprache.EN, value.getSprache());

		value.setSprache(Sprache.DE);
		assertFalse(value.isEqual(ProjektValue.parseJson(json)));

		value.setSprache(Sprache.EN);
		assertTrue(value.isEqual(ProjektValue.parseJson(json)));
	}

	@Test
	public void jsonBesitzer() {
		final UUID uuid = UUID.randomUUID();
		final String name = "Projekt A";
		final String json = "{" +
				"\"name\": \"" + name + "\"," +
				"\"besitzer\": null" +
				"}";
		final ProjektValue value = ProjektValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertNull(value.getBesitzer());
		assertTrue(value.isEqual(ProjektValue.parseJson(value.writeJson())));

		final NutzerValue nutzer = new NutzerValue(0L, uuid);
		value.setBesitzer(nutzer);
		assertSame(nutzer, value.getBesitzer());
		assertTrue(value.isEqual(ProjektValue.parseJson(value.writeJson())));
	}

	@Test
	public void jsonMitglied() {
		final UUID uuid = UUID.randomUUID();
		final String name = "Projekt A";
		final String json = "{" +
				"\"name\": \"" + name + "\"," +
				"\"allMitglied\": []" +
				"}";
		final ProjektValue value = ProjektValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(name, value.getName());
		assertEquals(0, value.getAllMitglied().size());
		assertTrue(value.isEqual(ProjektValue.parseJson(value.writeJson())));

		final NutzerValue nutzer = new NutzerValue(0L, uuid);
		value.addMitglied(nutzer);
		assertEquals(1, value.getAllMitglied().size());
		assertEquals(1, value.getAllMitglied().stream()
				.filter(e -> e.getDataId().equals(uuid))
				.count());
		assertTrue(value.isEqual(ProjektValue.parseJson(value.writeJson())));
	}
}
