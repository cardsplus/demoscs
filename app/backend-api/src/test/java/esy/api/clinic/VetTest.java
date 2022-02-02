package esy.api.clinic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class VetTest {

	static Vet createWithName(final String name) {
		final String json = "{" +
				"\"name\":\"" + name + "\"" +
				"}";
		return Vet.parseJson(json);
	}

	@Test
	void equalsHashcodeToString() {
		final String name = "Max Mustermann";
		final Vet value = createWithName(name);
		// Identisches Objekt
		assertEquals(value, value);
		assertTrue(value.isEqual(value));
		assertEquals(value.hashCode(), value.hashCode());
		assertEquals(value.toString(), value.toString());
		// Gleiches Objekt
		final Vet clone = Vet.parseJson(value.writeJson());
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
		final String name = "Max Mustermann";
		final Vet value0 = createWithName(name);
		final Vet value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final Vet value2 = value0.withId(UUID.randomUUID());
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
		final String name = "Max Mustermann";
		final String json = "{" +
				"\"" + key + "\": \"" + name + "\"," +
				"\"name\":\"" + name + "\"" +
				"}";
		final Vet value = Vet.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertNotNull(value.getId());
		assertEquals(name, value.getName());
	}

	@Test
	void json() {
		final String name = "Max.Mustermann";
		final Vet value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertNotNull(value.getId());
		assertEquals(name, value.getName());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{}",
			"{\"name\": \"\"}",
			"{\"name\": \"\", \"mail\": \"Max.Mustermann@a.de\"}",
			"{\"name\": \" \", \"mail\": \"Max.Mustermann@a.de\"}",
			"{\"name\": \"\\t\", \"mail\": \"Max.Mustermann@a.de\"}"
	})
	void jsonNameConstraints(final String json) {
		final Vet value = Vet.parseJson(json);
		assertThrows(IllegalArgumentException.class, value::verify);
		assertTrue(value.getName().isBlank());
	}
}
