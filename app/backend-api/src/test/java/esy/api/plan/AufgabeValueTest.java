package esy.api.plan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AufgabeValueTest {

	static AufgabeValue createWithText(final String text) {
		return AufgabeValue.parseJson("{" +
				"\"version\": \"1\"," +
				"\"text\": \"" + text + "\"," +
				"\"aktiv\": \"false\"" +
				"}");
	}

	@Test
	void equalsHashcodeToString() {
		final String text = "Aufgabe A";
		final AufgabeValue value = createWithText(text);
		// Identisches Objekt
		assertEquals(value, value);
		assertTrue(value.isEqual(value));
		assertEquals(value.hashCode(), value.hashCode());
		assertEquals(value.toString(), value.toString());
		// Gleiches Objekt
		final AufgabeValue clone = createWithText(text);
		assertNotSame(clone, value);
		assertTrue(clone.isEqual(value));
		assertNotEquals(clone.hashCode(), value.hashCode());
		assertNotEquals(clone.toString(), value.toString());
		// Anderes Objekt
		final AufgabeValue other = createWithText("X" + text);
		assertNotSame(other, value);
		assertNotEquals(other, value);
		assertFalse(value.isEqual(other));
		assertNotEquals(other.hashCode(), value.hashCode());
		assertNotEquals(other.toString(), value.toString());
		// Kein Objekt
		assertNotEquals(value, null);
		assertFalse(value.isEqual(null));
		// Falsches Objekt
		assertNotEquals(this, value);
	}

	@Test
	void withId() {
		final String text = "Aufgabe A";
		final AufgabeValue value0 = createWithText(text);
		final AufgabeValue value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final AufgabeValue value2 = value0.withId(UUID.randomUUID());
		assertNotSame(value0, value2);
		assertTrue(value0.isEqual(value2));
	}

	@Test
	void json() {
		final String text = "Aufgabe A";
		final AufgabeValue value = createWithText(text);
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
	void jsonConstraints(final String json) {
		final AufgabeValue value = AufgabeValue.parseJson(json);
		assertThrows(IllegalArgumentException.class, value::verify);
	}

	@Test
	public void jsonProjekt() {
		final String text = "Aufgabe A";
		final AufgabeValue value = createWithText(text);
		assertDoesNotThrow(value::verify);
		assertNull(value.getProjekt());

		final ProjektValue projekt = ProjektValue.parseJson("{" +
				"\"name\": \"Projekt A\"" +
				"}");
		value.setProjekt(projekt);
		assertDoesNotThrow(value::verify);
		assertSame(projekt, value.getProjekt());

		value.setProjekt(null);
		assertDoesNotThrow(value::verify);
		assertNull(value.getProjekt());
	}
}
