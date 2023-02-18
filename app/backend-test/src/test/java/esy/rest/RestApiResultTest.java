package esy.rest;

import esy.api.info.Enum;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("fast")
@ExtendWith(MockitoExtension.class)
class RestApiResultTest {

    @Test
    void constraints() throws IOException {
        final String json = "";
        final HttpURLConnection conn = mock(HttpURLConnection.class);
        when(conn.getResponseCode())
                .thenReturn(200);
        when(conn.getInputStream())
                .thenReturn(new ByteArrayInputStream(json.getBytes()));
        when(conn.getHeaderField("ETag"))
                .thenReturn(null);
        final RestApiResult classUnderTest = new RestApiResult(conn);
        assertEquals(200, classUnderTest.getCode());
        assertNotNull(classUnderTest.getBody());
        assertNotNull(classUnderTest.getEtag());
        assertEquals(json, classUnderTest.toString());

        final Enum value = classUnderTest.toObject(Enum.class);
        assertNull(value);

        final List<Enum> allValue = classUnderTest.toCollection(Enum.class);
        assertNotNull(allValue);
        assertEquals(0, allValue.size());
    }

    @Test
    void toObject() throws IOException {
        final String json = "{\n" +
                "  \"id\" : \"debac55c-0a62-4856-ae75-4b4b58f831c8\",\n" +
                "  \"art\" : \"SPRACHE\",\n" +
                "  \"code\" : 0,\n" +
                "  \"name\" : \"DE\",\n" +
                "  \"text\" : \"Deutsch\"\n" +
                "}";
        final HttpURLConnection conn = mock(HttpURLConnection.class);
        when(conn.getResponseCode())
                .thenReturn(200);
        when(conn.getInputStream())
                .thenReturn(new ByteArrayInputStream(json.getBytes()));
        when(conn.getHeaderField("ETag"))
                .thenReturn("\"3\"");
        final RestApiResult classUnderTest = new RestApiResult(conn);
        assertEquals(200, classUnderTest.getCode());
        assertNotNull(classUnderTest.getBody());
        assertNotNull(classUnderTest.getEtag());
        assertEquals(json, classUnderTest.toString());

        final Enum value = classUnderTest.toObject(Enum.class);
        assertNotNull(value);
        assertNotNull(value.getId());
        assertEquals("SPRACHE", value.getArt());
        assertEquals(0L, value.getCode());
        assertEquals("DE", value.getName());
        assertEquals("Deutsch", value.getText());
        // version comes from ETag header
        assertEquals(3, value.getVersion());
    }

    @Test
    void toCollection() throws IOException {
        final String json = "{\"content\":[{\n" +
                "  \"id\" : \"debac55c-dead-beef-dead-deadbeefdead\",\n" +
                "  \"art\" : \"SPRACHE\",\n" +
                "  \"code\" : 0,\n" +
                "  \"name\" : \"DE\",\n" +
                "  \"text\" : \"Deutsch\"\n" +
                "},{\n" +
                "  \"id\" : \"ee4b58f8-dead-beef-dead-deadbeefdead\",\n" +
                "  \"art\" : \"SPRACHE\",\n" +
                "  \"code\" : 1,\n" +
                "  \"name\" : \"EN\",\n" +
                "  \"text\" : \"Englisch\"\n" +
                "}]}";
        final HttpURLConnection conn = mock(HttpURLConnection.class);
        when(conn.getResponseCode())
                .thenReturn(200);
        when(conn.getInputStream())
                .thenReturn(new ByteArrayInputStream(json.getBytes()));
        when(conn.getHeaderField("ETag"))
                .thenReturn(null);
        final RestApiResult classUnderTest = new RestApiResult(conn);
        assertEquals(200, classUnderTest.getCode());
        assertNotNull(classUnderTest.getBody());
        assertNotNull(classUnderTest.getEtag());
        assertEquals(json, classUnderTest.toString());

        final List<Enum> allValue = classUnderTest.toCollection(Enum.class);
        assertNotNull(allValue);
        assertEquals(2, allValue.size());

        var value0 = allValue.get(0);
        assertNotNull(value0.getId());
        assertEquals("SPRACHE", value0.getArt());
        assertEquals(0L, value0.getCode());
        assertEquals("DE", value0.getName());
        assertEquals("Deutsch", value0.getText());
        // version comes from ETag header
        assertEquals(0, value0.getVersion());

        var value1 = allValue.get(1);
        assertNotNull(value1.getId());
        assertEquals("SPRACHE", value1.getArt());
        assertEquals(1L, value1.getCode());
        assertEquals("EN", value1.getName());
        assertEquals("Englisch", value1.getText());
        // version comes from ETag header
        assertEquals(0, value1.getVersion());
    }
}
