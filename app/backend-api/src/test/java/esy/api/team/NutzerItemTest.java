package esy.api.team;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class NutzerItemTest {

    NutzerValue createWithName(final String name) {
        return NutzerValue.parseJson("{" +
                "\"version\": \"1\"," +
                "\"mail\": \"" + name + "@a.de\"," +
                "\"name\":\"" + name + "\"," +
                "\"aktiv\": \"false\"," +
                "\"allSprache\": [\"EN\"]" +
                "}");
    }

    @Test
    void equalsHashcodeToString() {
        final String name = "Max Mustermann";
        final NutzerItem value = NutzerItem.fromValue(createWithName(name));
        // Identisches Objekt
        assertEquals(value, value);
        assertEquals(value.hashCode(), value.hashCode());
        assertEquals(value.toString(), value.toString());
        // Gleiches Objekt
        final NutzerItem clone = NutzerItem.fromValue(createWithName(name));
        assertNotSame(clone, value);
        assertNotEquals(clone, value);
        assertNotEquals(clone.hashCode(), value.hashCode());
        assertEquals(clone.toString(), value.toString());
        // Anderes Objekt
        final NutzerItem other = NutzerItem.fromValue(createWithName("X" + name));
        assertNotSame(other, value);
        assertNotEquals(other, value);
        assertNotEquals(other.hashCode(), value.hashCode());
        assertNotEquals(other.toString(), value.toString());
        // Kein Objekt
        assertNotEquals(value, null);
        // Falsches Objekt
        assertNotEquals(this, value);
    }

    @Test
    void ofNull() {
        final NutzerItem item = NutzerItem.fromValue(null);
        assertNull(item.getValue());
        assertTrue(item.getText().isEmpty());
        assertFalse(item.isCreate());
        assertFalse(item.isUpdate());
        assertTrue(item.isDelete());
    }

    @Test
    void ofValue() {
        final String name = "Max.Mustermann";
        final NutzerValue value = createWithName(name);
        final NutzerItem item = NutzerItem.fromValue(value);
        assertEquals(item.getValue(), value.getId());
        assertEquals(item.getText(), value.getName() + " <" + value.getMail() + ">");
        assertFalse(item.isCreate());
        assertTrue(item.isUpdate());
        assertFalse(item.isDelete());
    }
}
