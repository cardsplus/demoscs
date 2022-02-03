package esy.api.client;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class OwnerItemTest {

    Owner createWithName(final String name) {
        final String json = "{" +
                "\"name\":\"" + name + "\"" +
                "}";
        return Owner.parseJson(json);
    }

    @Test
    void equalsHashcodeToString() {
        final String name = "Max Mustermann";
        final Owner value = createWithName(name);
        final OwnerItem item0 = OwnerItem.fromValue(value);
        // Identisches Objekt
        assertEquals(item0, item0);
        assertEquals(item0.hashCode(), item0.hashCode());
        assertEquals(item0.toString(), item0.toString());
        // Gleiches Objekt
        final OwnerItem item1 = OwnerItem.fromValue(value);
        assertEquals(item1, item0);
        assertEquals(item1.hashCode(), item0.hashCode());
        assertEquals(item1.toString(), item0.toString());
        // Gleicher Name
        final OwnerItem item2 = OwnerItem.fromValue(createWithName(name));
        assertNotEquals(item2, item0);
        assertNotEquals(item2.hashCode(), item0.hashCode());
        assertEquals(item2.toString(), item0.toString());
        // Anderer Name
        final OwnerItem item3 = OwnerItem.fromValue(createWithName("X" + name));
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
        final OwnerItem item = OwnerItem.fromValue(null);
        assertNull(item.getValue());
        assertTrue(item.getText().isEmpty());
        assertFalse(item.isCreate());
        assertFalse(item.isUpdate());
        assertTrue(item.isDelete());
    }

    @Test
    void ofValue() {
        final String name = "Max Mustermann";
        final Owner value = createWithName(name);
        final OwnerItem item = OwnerItem.fromValue(value);
        assertEquals(value.getId(), item.getValue());
        assertEquals(value.getName(), item.getText());
        assertFalse(item.isCreate());
        assertTrue(item.isUpdate());
        assertFalse(item.isDelete());
    }
}
