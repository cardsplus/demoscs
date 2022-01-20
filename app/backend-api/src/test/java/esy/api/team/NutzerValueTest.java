package esy.api.team;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class NutzerValueTest {

	static NutzerValue createWithName(final String name) {
		final String json = "{" +
				"\"version\": \"1\"," +
				"\"mail\": \"" + name + "@a.de\"," +
				"\"name\":\"" + name + "\"," +
				"\"aktiv\": \"false\"," +
				"\"allSprache\": [\"EN\"]" +
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
	void withId() {
		final String name = "Max.Mustermann";
		final NutzerValue value0 = createWithName(name);
		final NutzerValue value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final NutzerValue value2 = value0.withId(UUID.randomUUID());
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
				"\"id\": \"" + uuid + "\"," +
				"\"mail\": \"" + name + "@a.de\"," +
				"\"name\": \"" + name + "\"," +
				"\"" + key + "\": \"" + name + "\"" +
				"}";
		final NutzerValue value = NutzerValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(1L, value.getVersion());
		assertNotNull(value.getId());
		assertEquals(name + "@a.de", value.getMail());
		assertEquals(name, value.getName());
		assertTrue(value.isAktiv());
		assertEquals(0, value.getAllSprache().size());
	}

	@Test
	void json() {
		final String name = "Max.Mustermann";
		final NutzerValue value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertEquals(1L, value.getVersion());
		assertNotNull(value.getId());
		assertEquals(name + "@a.de", value.getMail());
		assertEquals(name, value.getName());
		assertFalse(value.isAktiv());
		assertEquals(1, value.getAllSprache().size());
		assertTrue(value.getAllSprache().contains(Sprache.EN.name()));
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
		assertDoesNotThrow(value::verify);
		assertFalse(value.isEqual(NutzerValue.parseJson(json).verify()));

		value.setName(name);
		assertDoesNotThrow(value::verify);
		assertTrue(value.isEqual(NutzerValue.parseJson(json).verify()));
	}

	@Test
	public void jsonAktiv() {
		final String name = "Max.Mustermann";
		final String json = "{" +
				"\"mail\": \"" + name + "@a.de\"," +
				"\"name\": \"" + name + "\"," +
				"\"aktiv\": \"false\"" +
				"}";
		final NutzerValue value = NutzerValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(name + "@a.de", value.getMail());
		assertEquals(name, value.getName());
		assertFalse(value.isAktiv());

		value.setAktiv(true);
		assertDoesNotThrow(value::verify);
		assertFalse(value.isEqual(NutzerValue.parseJson(json).verify()));

		value.setAktiv(false);
		assertDoesNotThrow(value::verify);
		assertTrue(value.isEqual(NutzerValue.parseJson(json).verify()));
	}

	@Test
	public void jsonSprache() {
		final String name = "Max.Mustermann";
		final String json = "{" +
				"\"mail\": \"" + name + "@a.de\"," +
				"\"name\": \"" + name + "\"," +
				"\"allSprache\": [" +
				"\"EN\"" +
				"]" +
				"}";
		final NutzerValue value = NutzerValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(name + "@a.de", value.getMail());
		assertEquals(name, value.getName());
		assertEquals(1, value.getAllSprache().size());
		assertTrue(value.getAllSprache().contains(Sprache.EN.name()));


		value.getAllSprache().addAll(Arrays.stream(Sprache.values()).map(Sprache::name).collect(Collectors.toSet()));
		assertDoesNotThrow(value::verify);
		assertEquals(4, value.getAllSprache().size());
		assertTrue(value.getAllSprache().contains(Sprache.DE.name()));
		assertTrue(value.getAllSprache().contains(Sprache.EN.name()));
		assertTrue(value.getAllSprache().contains(Sprache.FR.name()));
		assertTrue(value.getAllSprache().contains(Sprache.IT.name()));
		assertFalse(value.isEqual(NutzerValue.parseJson(json).verify()));

		value.getAllSprache().clear();
		assertDoesNotThrow(value::verify);
		assertEquals(0, value.getAllSprache().size());
		assertFalse(value.isEqual(NutzerValue.parseJson(json).verify()));

		value.getAllSprache().clear();
		value.getAllSprache().add(Sprache.EN.name());
		assertEquals(1, value.getAllSprache().size());
		assertTrue(value.getAllSprache().contains(Sprache.EN.name()));
		assertTrue(value.isEqual(NutzerValue.parseJson(json).verify()));
	}
}
