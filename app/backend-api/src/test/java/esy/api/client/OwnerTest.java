package esy.api.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OwnerTest {

	static Owner createWithName(final String name) {
		final String json = "{" +
				"\"name\":\"" + name + "\"," +
				"\"address\":\"Bergweg 1, 5400 Hallein\"," +
				"\"contact\":\"+43 660 5557683\"" +
				"}";
		return Owner.parseJson(json);
	}

	@Test
	void equalsHashcodeToString() {
		final String name = "Max Mustermann";
		final Owner value = createWithName(name);
		// Identisches Objekt
		assertEquals(value, value);
		assertTrue(value.isEqual(value));
		assertEquals(value.hashCode(), value.hashCode());
		assertEquals(value.toString(), value.toString());
		// Gleiches Objekt
		final Owner clone = Owner.parseJson(value.writeJson());
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
		final Owner value0 = createWithName(name);
		final Owner value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final Owner value2 = value0.withId(UUID.randomUUID());
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
				"\"name\":\"" + name + "\"," +
				"\"address\":\"Bergweg 1, 5400 Hallein\"," +
				"\"contact\":\"+43 660 5557683\"" +
				"}";
		final Owner value = Owner.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertNotNull(value.getId());
		assertEquals(name, value.getName());
		assertFalse(value.getAddress().isBlank());
		assertFalse(value.getContact().isBlank());
	}

	@Test
	void json() {
		final String name = "Max Mustermann";
		final Owner value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertNotNull(value.getId());
		assertEquals(name, value.getName());
		assertFalse(value.getAddress().isBlank());
		assertFalse(value.getContact().isBlank());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{}",
			"{\"name\": \"\"}",
			"{\"name\": \" \"}",
			"{\"name\": \"\\t\"}"
	})
	void jsonConstraints(final String json) {
		final Owner value = Owner.parseJson(json);
		assertThrows(IllegalArgumentException.class, value::verify);
	}
}
