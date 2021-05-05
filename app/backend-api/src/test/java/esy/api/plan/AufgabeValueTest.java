package esy.api.plan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AufgabeValueTest {

	static AufgabeValue createWithName(final String name) {
		final String json = "{" +
				"\"version\": \"1\"," +
				"\"text\": \"" + name + "\"," +
				"\"aktiv\": \"false\"" +
				"}";
		return AufgabeValue.parseJson(json);
	}

	@Test
	void equalsHashcodeToString() {
		final String name = "Team A";
		final AufgabeValue value = createWithName(name);
		// Identisches Objekt
		assertEquals(value, value);
		assertTrue(value.isEqual(value));
		assertEquals(value.hashCode(), value.hashCode());
		assertEquals(value.toString(), value.toString());
		// Gleiches Objekt
		final AufgabeValue clone = AufgabeValue.parseJson(value.writeJson());
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
		final String name = "Team A";
		final AufgabeValue value0 = createWithName(name);
		final AufgabeValue value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final AufgabeValue value2 = value0.withId(UUID.randomUUID());
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
		final String text = "Aufgabe A";
		final UUID uuid = UUID.randomUUID();
		final String json = "{" +
				"\"version\": \"1\"," +
				"\"id\": \"" + uuid + "\"," +
				"\"text\": \"" + text + "\"," +
				"\"" + key + "\": \"" + text + "\"" +
				"}";
		final AufgabeValue value = AufgabeValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(1L, value.getVersion());
		assertNotNull(value.getId());
		assertEquals(text, value.getText());
		assertTrue(value.isAktiv());
	}

	@Test
	void json() {
		final String text = "Aufgabe A";
		final AufgabeValue value = createWithName(text);
		assertDoesNotThrow(value::verify);
		assertEquals(1L, value.getVersion());
		assertNotNull(value.getId());
		assertEquals(text, value.getText());
		assertFalse(value.isAktiv());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{}",
			"{\"text\": \"\"}",
			"{\"aktiv\": \"false\"}"
	})
	void jsonTextConstraints(final String json) {
		final AufgabeValue value = AufgabeValue.parseJson(json);
		assertThrows(IllegalArgumentException.class, value::verify);
		assertTrue(value.getText().isBlank());
	}

	@Test
	public void jsonText() {
		final String text = "Aufgabe A";
		final String json = "{" +
				"\"text\": \"" + text + "\"" +
				"}";
		final AufgabeValue value = AufgabeValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(text, value.getText());
		assertTrue(value.isAktiv());

		assertThrows(NullPointerException.class, () -> value.setText(null));

		value.setText("X" + text);
		assertDoesNotThrow(value::verify);
		assertFalse(value.isEqual(AufgabeValue.parseJson(json).verify()));

		value.setText(text);
		assertDoesNotThrow(value::verify);
		assertTrue(value.isEqual(AufgabeValue.parseJson(json).verify()));
	}

	@Test
	public void jsonAktiv() {
		final String text = "Aufgabe A";
		final String json = "{" +
				"\"text\": \"" + text + "\"," +
				"\"aktiv\": \"false\"" +
				"}";
		final AufgabeValue value = AufgabeValue.parseJson(json);
		assertDoesNotThrow(value::verify);
		assertEquals(text, value.getText());
		assertFalse(value.isAktiv());

		value.setAktiv(true);
		assertDoesNotThrow(value::verify);
		assertFalse(value.isEqual(AufgabeValue.parseJson(json).verify()));

		value.setAktiv(false);
		assertDoesNotThrow(value::verify);
		assertTrue(value.isEqual(AufgabeValue.parseJson(json).verify()));
	}
}
