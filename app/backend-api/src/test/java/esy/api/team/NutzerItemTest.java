package esy.api.team;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class NutzerItemTest {

    NutzerValue createWithName(final String name) {
        final String json = "{" +
                "\"mail\": \"" + name + "@a.de\"," +
                "\"name\":\"" + name + "\"," +
                "\"aktiv\": \"true\"" +
                "}";
        return NutzerValue.parseJson(json);
    }

    @Test
    void equalsHashcodeToString() {
        final String name = "Max.Mustermann";
        final NutzerValue value = createWithName(name);
        final NutzerItem item0 = NutzerItem.fromValue(value);
        // Identisches Objekt
        assertEquals(item0, item0);
        assertEquals(item0.hashCode(), item0.hashCode());
        assertEquals(item0.toString(), item0.toString());
        // Gleiches Objekt
        final NutzerItem item1 = NutzerItem.fromValue(value);
        assertEquals(item1, item0);
        assertEquals(item1.hashCode(), item0.hashCode());
        assertEquals(item1.toString(), item0.toString());
        // Gleicher Name
        final NutzerItem item2 = NutzerItem.fromValue(createWithName(name));
        assertNotEquals(item2, item0);
        assertNotEquals(item2.hashCode(), item0.hashCode());
        assertEquals(item2.toString(), item0.toString());
        // Anderer Name
        final NutzerItem item3 = NutzerItem.fromValue(createWithName("X" + name));
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
