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
		assertEquals(1, value.getAllSprache().size());
		assertTrue(value.getAllSprache().contains(Sprache.DE));
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
		assertEquals(2, value.getAllSprache().size());
		assertTrue(value.getAllSprache().contains(Sprache.DE));
		assertTrue(value.getAllSprache().contains(Sprache.EN));
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
		assertFalse(value.isEqual(NutzerValue.parseJson(json)));

		value.setAktiv(false);
		assertTrue(value.isEqual(NutzerValue.parseJson(json)));
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
		assertEquals(2, value.getAllSprache().size());
		assertTrue(value.getAllSprache().contains(Sprache.DE));
		assertTrue(value.getAllSprache().contains(Sprache.EN));

		value.getAllSprache().addAll(List.of(Sprache.values()));
		assertEquals(4, value.getAllSprache().size());
		assertTrue(value.getAllSprache().contains(Sprache.DE));
		assertTrue(value.getAllSprache().contains(Sprache.EN));
		assertTrue(value.getAllSprache().contains(Sprache.FR));
		assertTrue(value.getAllSprache().contains(Sprache.IT));
		assertFalse(value.isEqual(NutzerValue.parseJson(json)));

		value.getAllSprache().clear();
		assertEquals(0, value.getAllSprache().size());
		assertFalse(value.isEqual(NutzerValue.parseJson(json)));

		value.getAllSprache().add(Sprache.DE);
		assertEquals(1, value.getAllSprache().size());
		assertTrue(value.getAllSprache().contains(Sprache.DE));
		assertFalse(value.isEqual(NutzerValue.parseJson(json)));

		value.getAllSprache().add(Sprache.EN);
		assertEquals(2, value.getAllSprache().size());
		assertTrue(value.getAllSprache().contains(Sprache.DE));
		assertTrue(value.getAllSprache().contains(Sprache.EN));
		assertTrue(value.isEqual(NutzerValue.parseJson(json)));
	}
}
