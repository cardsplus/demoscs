package esy.api.clinic;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class VetItemTest {

    Vet createWithName(final String name) {
        final String json = "{" +
                "\"name\":\"" + name + "\"" +
                "}";
        return Vet.parseJson(json);
    }

    @Test
    void equalsHashcodeToString() {
        final String name = "Max Mustermann";
        final Vet value = createWithName(name);
        final VetItem item0 = VetItem.fromValue(value);
        // Identisches Objekt
        assertEquals(item0, item0);
        assertEquals(item0.hashCode(), item0.hashCode());
        assertEquals(item0.toString(), item0.toString());
        // Gleiches Objekt
        final VetItem item1 = VetItem.fromValue(value);
        assertEquals(item1, item0);
        assertEquals(item1.hashCode(), item0.hashCode());
        assertEquals(item1.toString(), item0.toString());
        // Gleicher Name
        final VetItem item2 = VetItem.fromValue(createWithName(name));
        assertNotEquals(item2, item0);
        assertNotEquals(item2.hashCode(), item0.hashCode());
        assertEquals(item2.toString(), item0.toString());
        // Anderer Name
        final VetItem item3 = VetItem.fromValue(createWithName("X" + name));
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
        final VetItem item = VetItem.fromValue(null);
        assertNull(item.getValue());
        assertTrue(item.getText().isEmpty());
        assertFalse(item.isCreate());
        assertFalse(item.isUpdate());
        assertTrue(item.isDelete());
    }

    @Test
    void ofValue() {
        final String name = "Max Mustermann";
        final Vet value = createWithName(name);
        final VetItem item = VetItem.fromValue(value);
        assertEquals(value.getId(), item.getValue());
        assertEquals(value.getName(), item.getText());
        assertFalse(item.isCreate());
        assertTrue(item.isUpdate());
        assertFalse(item.isDelete());
    }
}
