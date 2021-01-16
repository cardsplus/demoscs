package esy.api.team;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class NutzerValueTest {

	static NutzerValue createWithName(final String name) {
		final UUID uuid = UUID.randomUUID();
		final String json = "{" +
				"\"version\": \"1\"," +
				"\"dataId\": \"" + uuid + "\"," +
				"\"mail\": \"" + name + "@a.de\"," +
				"\"name\":\"" + name + "\"," +
				"\"aktiv\": \"false\"," +
				"\"allRolle\": [\"BEARBEITER\"]" +
				"}";
		return NutzerValue.parseJson(json);
	}

	@Test
	void equalsHashcodeToString() {
		final String name = "Max.Mustermann";
		final NutzerValue value = createWithName(name);
		// Identisches Objekt
		assertEquals(value, value);
		assertTrue(value.isEqual(value));
		assertEquals(value.hashCode(), value.hashCode());
		assertEquals(value.toString(), value.toString());
		// Gleiches Objekt
		final NutzerValue clone = NutzerValue.parseJson(value.writeJson());
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
		final String name = "Max.Mustermann";
		final NutzerValue value0 = createWithName(name);
		final NutzerValue value1 = value0.withDataId(value0.getDataId());
		assertSame(value0, value1);
		final NutzerValue value2 = value0.withDataId(UUID.randomUUID());
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
		final String name = "Max.Mustermann";
		final UUID uuid = UUID.randomUUID();
		final String json = "{" +
				"\"version\": \"1\"," +
				"\"dataId\": \"" + uuid + "\"," +
				"\"mail\": \"" + name + "@a.de\"," +
				"\"name\": \"" + name + "\"," +
				"\"" + key + "\": \"" + name + "\"" +
				"}";
		final NutzerValue value = NutzerValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(1L, value.getVersion());
		assertNotNull(value.getDataId());
		assertEquals(name + "@a.de", value.getMail());
		assertEquals(name, value.getName());
		assertTrue(value.isAktiv());
		assertEquals(1, value.getAllRolle().size());
		assertTrue(value.getAllRolle().contains(NutzerRolle.BESUCHER));
	}

	@Test
	void json() {
		final String name = "Max.Mustermann";
		final NutzerValue value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertEquals(1L, value.getVersion());
		assertNotNull(value.getDataId());
		assertEquals(name + "@a.de", value.getMail());
		assertEquals(name, value.getName());
		assertFalse(value.isAktiv());
		assertEquals(2, value.getAllRolle().size());
		assertTrue(value.getAllRolle().contains(NutzerRolle.BESUCHER));
		assertTrue(value.getAllRolle().contains(NutzerRolle.BEARBEITER));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{}",
			"{\"aktiv\": \"false\"}",
			"{\"mail\": \"\"}",
			"{\"mail\": \"\", \"name\": \"Max.Mustermann\"}",
			"{\"mail\": \" \", \"name\": \"Max.Mustermann\"}",
			"{\"mail\": \"\\t\", \"name\": \"Max.Mustermann\"}"
	})
	void jsonMailConstraints(final String json) {
		final NutzerValue value = NutzerValue.parseJson(json);
		assertThrows(IllegalArgumentException.class, value::verify);
		assertTrue(value.getMail().isBlank());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{}",
			"{\"aktiv\": \"false\"}",
			"{\"name\": \"\"}",
			"{\"name\": \"\", \"mail\": \"Max.Mustermann@a.de\"}",
			"{\"name\": \" \", \"mail\": \"Max.Mustermann@a.de\"}",
			"{\"name\": \"\\t\", \"mail\": \"Max.Mustermann@a.de\"}"
	})
	void jsonNameConstraints(final String json) {
		final NutzerValue value = NutzerValue.parseJson(json);
		assertThrows(IllegalArgumentException.class, value::verify);
		assertTrue(value.getName().isBlank());
	}

	@Test
	public void jsonName() {
		final String name = "Max.Mustermann";
		final String json = "{" +
				"\"mail\": \"" + name + "@a.de\"," +
				"\"name\": \"" + name + "\"" +
				"}";
		final NutzerValue value = NutzerValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(name + "@a.de", value.getMail());
		assertEquals(name, value.getName());

		assertThrows(NullPointerException.class, () -> value.setName(null));

		value.setName(name.replace('.', ' '));
		assertFalse(value.isEqual(NutzerValue.parseJson(json)));

		value.setName(name);
		assertTrue(value.isEqual(NutzerValue.parseJson(json)));
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	public void jsonAktiv(final boolean aktiv) {
		final String name = "Max.Mustermann";
		final String json = "{" +
				"\"mail\": \"" + name + "@a.de\"," +
				"\"name\": \"" + name + "\"," +
				"\"aktiv\": \"" + aktiv + "\"" +
				"}";
		final NutzerValue value = NutzerValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(name + "@a.de", value.getMail());
		assertEquals(name, value.getName());
		assertEquals(aktiv, value.isAktiv());

		value.setAktiv(!aktiv);
		assertFalse(value.isEqual(NutzerValue.parseJson(json)));

		value.setAktiv(aktiv);
		assertTrue(value.isEqual(NutzerValue.parseJson(json)));
	}

	@Test
	public void jsonRolle() {
		final String name = "Max.Mustermann";
		final String json = "{" +
				"\"mail\": \"" + name + "@a.de\"," +
				"\"name\": \"" + name + "\"," +
				"\"allRolle\": [" +
				"\"BEARBEITER\"" +
				"]" +
				"}";
		final NutzerValue value = NutzerValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(name + "@a.de", value.getMail());
		assertEquals(name, value.getName());
		assertEquals(2, value.getAllRolle().size());
		assertTrue(value.getAllRolle().contains(NutzerRolle.BESUCHER));
		assertTrue(value.getAllRolle().contains(NutzerRolle.BEARBEITER));

		value.getAllRolle().addAll(List.of(NutzerRolle.values()));
		assertEquals(4, value.getAllRolle().size());
		assertTrue(value.getAllRolle().contains(NutzerRolle.BESUCHER));
		assertTrue(value.getAllRolle().contains(NutzerRolle.BEARBEITER));
		assertTrue(value.getAllRolle().contains(NutzerRolle.VERWALTER));
		assertTrue(value.getAllRolle().contains(NutzerRolle.ADMINISTRATOR));
		assertFalse(value.isEqual(NutzerValue.parseJson(json)));

		value.getAllRolle().clear();
		assertEquals(0, value.getAllRolle().size());
		assertFalse(value.isEqual(NutzerValue.parseJson(json)));

		value.getAllRolle().add(NutzerRolle.BESUCHER);
		assertEquals(1, value.getAllRolle().size());
		assertTrue(value.getAllRolle().contains(NutzerRolle.BESUCHER));
		assertFalse(value.isEqual(NutzerValue.parseJson(json)));

		value.getAllRolle().add(NutzerRolle.BEARBEITER);
		assertEquals(2, value.getAllRolle().size());
		assertTrue(value.getAllRolle().contains(NutzerRolle.BESUCHER));
		assertTrue(value.getAllRolle().contains(NutzerRolle.BEARBEITER));
		assertTrue(value.isEqual(NutzerValue.parseJson(json)));
	}
}
