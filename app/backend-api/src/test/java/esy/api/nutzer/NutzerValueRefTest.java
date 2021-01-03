package esy.api.nutzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class NutzerValueRefTest {

    @Test
    void equalsHashcodeToString() {
        final UUID uuid = UUID.randomUUID();
        final NutzerValueRef value = new NutzerValueRef(uuid);
        // Identisches Objekt
        assertEquals(value, value);
        assertEquals(value.hashCode(), value.hashCode());
        assertEquals(value.toString(), value.toString());
        // Gleiches Objekt
        final NutzerValueRef clone = NutzerValueRef.parseJson(value.writeJson());
        assertEquals(clone.hashCode(), value.hashCode());
        assertEquals(clone.toString(), value.toString());
        // Gleiche UUID
        assertEquals(new NutzerValueRef(uuid), value);
        assertEquals(new NutzerValueRef(uuid).hashCode(), value.hashCode());
        assertEquals(new NutzerValueRef(uuid).toString(), value.toString());
        // Andere UUID
        assertNotEquals(new NutzerValueRef(UUID.randomUUID()), value);
        assertNotEquals(new NutzerValueRef(UUID.randomUUID()).hashCode(), value.hashCode());
        assertNotEquals(new NutzerValueRef(UUID.randomUUID()).toString(), value.toString());
        // Kein Objekt
        assertNotEquals(value, null);
        // Falsches Objekt
        assertNotEquals(this, value);
    }

    @Test
    void withRefId() {
        final NutzerValueRef value0 = new NutzerValueRef(UUID.randomUUID());
        final NutzerValueRef value1 = value0.withRefId(value0.getRefId());
        assertSame(value0, value1);
        final NutzerValueRef value2 = value0.withRefId(UUID.randomUUID());
        assertNotSame(value0, value2);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "metaId",
            "metaCreated",
            "garbage"
    })
    void jsonGarbage(final String key) {
        final UUID uuid = UUID.randomUUID();
        final String name = "Team A";
        final String json = "{" +
                "\"" + key + "\": \"" + name + "\"," +
                "\"refId\": \"" + uuid + "\"" +
                "}";
        final NutzerValueRef value = NutzerValueRef.parseJson(json);
        assertNotNull(value.getRefId());
    }

    @Test
    void json() {
        final UUID uuid = UUID.randomUUID();
        final String json = "{" +
                "\"refId\": \"" + uuid + "\"" +
                "}";
        final NutzerValueRef value = NutzerValueRef.parseJson(json);
        assertEquals(uuid, value.getRefId());
    }
}
