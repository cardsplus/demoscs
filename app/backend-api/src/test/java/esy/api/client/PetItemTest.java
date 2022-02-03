package esy.api.client;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class PetItemTest {

    Pet createWithName(final String name) {
        final String json = "{" +
                "\"name\":\"" + name + "\"," +
                "\"born\":\"2021-04-22\"," +
                "\"species\":\"Cat\"" +
                "}";
        return Pet.parseJson(json);
    }

    @Test
    void equalsHashcodeToString() {
        final String name = "Tom";
        final Pet value = createWithName(name);
        final PetItem item0 = PetItem.fromValue(value);
        // Identisches Objekt
        assertEquals(item0, item0);
        assertEquals(item0.hashCode(), item0.hashCode());
        assertEquals(item0.toString(), item0.toString());
        // Gleiches Objekt
        final PetItem item1 = PetItem.fromValue(value);
        assertEquals(item1, item0);
        assertEquals(item1.hashCode(), item0.hashCode());
        assertEquals(item1.toString(), item0.toString());
        // Gleicher Name
        final PetItem item2 = PetItem.fromValue(createWithName(name));
        assertNotEquals(item2, item0);
        assertNotEquals(item2.hashCode(), item0.hashCode());
        assertEquals(item2.toString(), item0.toString());
        // Anderer Name
        final PetItem item3 = PetItem.fromValue(createWithName("X" + name));
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
        final PetItem item = PetItem.fromValue(null);
        assertNull(item.getValue());
        assertTrue(item.getText().isEmpty());
        assertFalse(item.isCreate());
        assertFalse(item.isUpdate());
        assertTrue(item.isDelete());
    }

    @Test
    void ofValue() {
        final String name = "Tom";
        final Pet value = createWithName(name);
        final PetItem item = PetItem.fromValue(value);
        assertEquals(value.getId(), item.getValue());
        assertEquals("Cat 'Tom'", item.getText());
        assertFalse(item.isCreate());
        assertTrue(item.isUpdate());
        assertFalse(item.isDelete());
    }
}
