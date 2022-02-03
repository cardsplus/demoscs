package esy.api.plan;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class ProjektItemTest {

    Projekt createWithName(final String name) {
        return Projekt.parseJson("{" +
                "\"version\": \"1\"," +
                "\"name\": \"" + name + "\"," +
                "\"aktiv\": \"false\"," +
                "\"sprache\": \"EN\"," +
                "\"besitzer\": null," +
                "\"allMitglied\": []" +
                "}");
    }

    @Test
    void equalsHashcodeToString() {
        final String name = "Projekt A";
        final ProjektItem value = ProjektItem.fromValue(createWithName(name));
        // Identisches Objekt
        assertEquals(value, value);
        assertEquals(value.hashCode(), value.hashCode());
        assertEquals(value.toString(), value.toString());
        // Gleiches Objekt
        final ProjektItem clone = ProjektItem.fromValue(createWithName(name));
        assertNotSame(clone, value);
        assertNotEquals(clone, value);
        assertNotEquals(clone.hashCode(), value.hashCode());
        assertEquals(clone.toString(), value.toString());
        // Anderes Objekt
        final ProjektItem other = ProjektItem.fromValue(createWithName("X" + name));
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
        final ProjektItem item = ProjektItem.fromValue(null);
        assertNull(item.getValue());
        assertTrue(item.getText().isEmpty());
        assertFalse(item.isCreate());
        assertFalse(item.isUpdate());
        assertTrue(item.isDelete());
    }

    @Test
    void ofValue() {
        final String name = "Projekt A";
        final Projekt value = createWithName(name);
        final ProjektItem item = ProjektItem.fromValue(value);
        assertEquals(item.getValue(), value.getId());
        assertEquals(item.getText(), value.getName());
        assertFalse(item.isCreate());
        assertTrue(item.isUpdate());
        assertFalse(item.isDelete());
    }
}
