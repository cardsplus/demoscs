package esy.api.plan;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class ProjektItemTest {

    ProjektValue createWithName(final String name) {
        final String json = "{" +
                "\"name\":\"" + name + "\"," +
                "\"aktiv\": \"true\"" +
                "}";
        return ProjektValue.parseJson(json);
    }

    @Test
    void equalsHashcodeToString() {
        final String name = "Projekt A";
        final ProjektValue value = createWithName(name);
        final ProjektItem item0 = ProjektItem.fromValue(value);
        // Identisches Objekt
        assertEquals(item0, item0);
        assertEquals(item0.hashCode(), item0.hashCode());
        assertEquals(item0.toString(), item0.toString());
        // Gleiches Objekt
        final ProjektItem item1 = ProjektItem.fromValue(value);
        assertEquals(item1, item0);
        assertEquals(item1.hashCode(), item0.hashCode());
        assertEquals(item1.toString(), item0.toString());
        // Gleicher Name
        final ProjektItem item2 = ProjektItem.fromValue(createWithName(name));
        assertNotEquals(item2, item0);
        assertNotEquals(item2.hashCode(), item0.hashCode());
        assertEquals(item2.toString(), item0.toString());
        // Anderer Name
        final ProjektItem item3 = ProjektItem.fromValue(createWithName("X" + name));
        assertNotEquals(item3, item0);
        assertNotEquals(item3.hashCode(), item0.hashCode());
        assertNotEquals(item3.toString(), item0.toString());
        // Kein Objekt
        assertNotEquals(null, item0);
        // Falsches Objekt
        assertNotEquals(this, item0);
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
        final ProjektValue value = createWithName(name);
        final ProjektItem item = ProjektItem.fromValue(value);
        assertEquals(item.getValue(), value.getId());
        assertEquals(item.getText(), value.getName());
        assertFalse(item.isCreate());
        assertTrue(item.isUpdate());
        assertFalse(item.isDelete());
    }
}
