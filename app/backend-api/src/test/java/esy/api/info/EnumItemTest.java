package esy.api.info;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class EnumItemTest {

    EnumValue createWithName(final String name) {
        final String json = "{" +
                "\"art\": \"QUELLE\"," +
                "\"name\": \"" + name + "\"," +
                "\"code\": \"2\"," +
                "\"text\": \"A " + name + "\"" +
                "}";
        return EnumValue.parseJson(json);
    }

    @Test
    void equalsHashcodeToString() {
        final String name = "JIRA";
        final EnumValue value = createWithName(name);
        final EnumItem item0 = EnumItem.fromValue(value);
        // Identisches Objekt
        assertEquals(item0, item0);
        assertEquals(item0.hashCode(), item0.hashCode());
        assertEquals(item0.toString(), item0.toString());
        // Gleiches Objekt
        final EnumItem item1 = EnumItem.fromValue(value);
        assertEquals(item1, item0);
        assertEquals(item1.hashCode(), item0.hashCode());
        assertEquals(item1.toString(), item0.toString());
        // Gleicher Name
        final EnumItem item2 = EnumItem.fromValue(createWithName(name));
        assertEquals(item2, item0);
        assertEquals(item2.hashCode(), item0.hashCode());
        assertEquals(item2.toString(), item0.toString());
        // Anderer Name
        final EnumItem item3 = EnumItem.fromValue(createWithName("X" + name));
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
        final EnumItem item = EnumItem.fromValue(null);
        assertNull(item.getValue());
        assertNull(item.getName());
        assertEquals("", item.getText());
        assertEquals(0L, item.getCode());
        assertFalse(item.isCreate());
        assertFalse(item.isUpdate());
        assertTrue(item.isDelete());
    }

    @Test
    void ofValue() {
        final String name = "JIRA";
        final EnumValue value = createWithName(name);
        final EnumItem item = EnumItem.fromValue(value);
        assertEquals(name, item.getValue());
        assertEquals(value.getName(), item.getName());
        assertEquals(value.getText(), item.getText());
        assertEquals(value.getCode(), item.getCode());
        assertFalse(item.isCreate());
        assertTrue(item.isUpdate());
        assertFalse(item.isDelete());
    }
}
