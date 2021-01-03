package esy.app.plan;

import esy.app.nutzer.NutzerValueRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("slow")
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
public class ProjektValueRestApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NutzerValueRepository nutzerValueRepository;

    @Autowired
    private ProjektValueRepository projektValueRepository;

    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext,
                      final RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST", "PUT", "DELETE"})
    @Order(1)
    void preflight(final String method) throws Exception {
        mockMvc.perform(options("/api/projekt")
                .header("Access-Control-Request-Method", method)
                .header("Access-Control-Request-Headers", "Content-Type")
                .header("Origin", "http://localhost:5000")) // UI
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(header()
                        .exists("Access-Control-Allow-Origin"))
                .andExpect(header()
                        .exists("Access-Control-Allow-Methods"))
                .andExpect(header()
                        .exists("Access-Control-Allow-Headers"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PATCH"})
    @Order(2)
    void preflightNotAllowed(final String method) throws Exception {
        mockMvc.perform(options("/api/projekt")
                .header("Access-Control-Request-Method", method)
                .header("Access-Control-Request-Headers", "Content-Type")
                .header("Origin", "http://localhost:5000")) // UI
                .andDo(print())
                .andExpect(status()
                        .isForbidden())
                .andExpect(header()
                        .doesNotExist("Access-Control-Allow-Origin"))
                .andExpect(header()
                        .doesNotExist("Access-Control-Allow-Methods"))
                .andExpect(header()
                        .doesNotExist("Access-Control-Allow-Headers"));
    }

    @Test
    @Order(10)
    void getApiProjektNoElement() throws Exception {
        projektValueRepository.deleteAll();
        mockMvc.perform(get("/api/projekt")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(jsonPath("$.content")
                        .isArray())
                .andExpect(jsonPath("$.content[0]")
                        .doesNotExist());
    }

    @Test
    @Order(20)
    void postApiProjekt() throws Exception {
        final String name = "Projekt A";
        assertFalse(projektValueRepository.findByName(name).isPresent());
        mockMvc.perform(post("/api/projekt")
                .content("{" +
                        "\"name\":\"" + name + "\"," +
                        "\"aktiv\": \"true\"" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isCreated())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"0\""))
                .andExpect(jsonPath("$.dataId")
                        .isNotEmpty())
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @Order(21)
    void postApiProjektConflict(final boolean aktiv) throws Exception {
        final String name = "Projekt A";
        assertTrue(projektValueRepository.findByName(name).isPresent());
        mockMvc.perform(post("/api/projekt")
                .content("{" +
                        "\"name\":\"" + name + "\"," +
                        "\"aktiv\": \"" + aktiv + "\"" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isConflict());
    }

    @Test
    @Order(22)
    void postApiProjektDefault() throws Exception {
        final String name = "Projekt B";
        assertFalse(projektValueRepository.findByName(name).isPresent());
        mockMvc.perform(post("/api/projekt")
                .content("{" +
                        "\"name\":\"" + name + "\"" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isCreated())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"0\""))
                .andExpect(jsonPath("$.dataId")
                        .isNotEmpty())
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"));
    }

    @Test
    @Order(30)
    void putApiProjekt() throws Exception {
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        final String name = "Projekt C";
        assertFalse(projektValueRepository.findById(UUID.fromString(uuid)).isPresent());
        assertFalse(projektValueRepository.findByName(name).isPresent());
        mockMvc.perform(put("/api/projekt/" + uuid)
                .content("{" +
                        "\"name\":\"" + name + "\"," +
                        "\"aktiv\": \"true\"" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isCreated())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"0\""))
                .andExpect(jsonPath("$.dataId")
                        .isNotEmpty())
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"));
    }

    @RepeatedTest(5)
    @Order(31)
    void putApiProjektAgain(final RepetitionInfo info) throws Exception {
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        final String name = "Projekt C";
        assertTrue(projektValueRepository.findById(UUID.fromString(uuid)).isPresent());
        assertTrue(projektValueRepository.findByName(name).isPresent());
        mockMvc.perform(put("/api/projekt/" + uuid)
                .content("{" +
                        "\"name\":\"" + name + "\"," +
                        "\"aktiv\": \"true\"" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"0\""))
                .andExpect(jsonPath("$.dataId")
                        .isNotEmpty())
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"));
    }

    @Test
    @Order(32)
    void putApiProjektDefault() throws Exception {
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        final String name = "Projekt C";
        assertTrue(projektValueRepository.findById(UUID.fromString(uuid)).isPresent());
        assertTrue(projektValueRepository.findByName(name).isPresent());
        mockMvc.perform(put("/api/projekt/" + uuid)
                .content("{" +
                        "\"name\":\"" + name + "\"" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"0\""))
                .andExpect(jsonPath("$.dataId")
                        .isNotEmpty())
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"));
    }

    @Test
    @Order(33)
    void putApiProjektAktiv() throws Exception {
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        final String name = "Projekt C";
        assertTrue(projektValueRepository.findById(UUID.fromString(uuid)).isPresent());
        assertTrue(projektValueRepository.findByName(name).isPresent());
        mockMvc.perform(put("/api/projekt/" + uuid)
                .content("{" +
                        "\"name\":\"" + name + "\"," +
                        "\"aktiv\": \"false\"" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"1\""))
                .andExpect(jsonPath("$.dataId")
                        .isNotEmpty())
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.aktiv")
                        .value("false"));
    }

    @Sql("/sql/nutzer.sql")
    @Test
    @Order(34)
    void putApiProjektBesitzer() throws Exception {
        final String nutzerUuid = "a1111111-6ee8-4335-b12a-ef84794bd27a";
        assertTrue(nutzerValueRepository.findById(UUID.fromString(nutzerUuid)).isPresent());
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        final String name = "Projekt C";
        assertTrue(projektValueRepository.findById(UUID.fromString(uuid)).isPresent());
        assertTrue(projektValueRepository.findByName(name).isPresent());
        mockMvc.perform(put("/api/projekt/" + uuid)
                .content("{" +
                        "\"name\":\"" + name + "\"," +
                        "\"besitzer\": {\"refId\": \"" + nutzerUuid + "\"}," +
                        "\"allMitglied\": [" +
                        "]" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"2\""))
                .andExpect(jsonPath("$.dataId")
                        .isNotEmpty())
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"))
                .andExpect(jsonPath("$.besitzer.refId")
                        .value(nutzerUuid))
                .andExpect(jsonPath("$.allMitglied[0]")
                        .doesNotExist());
    }

    @Test
    @Order(35)
    void putApiProjektMitglied() throws Exception {
        final String nutzerUuid = "a1111111-6ee8-4335-b12a-ef84794bd27a";
        assertTrue(nutzerValueRepository.findById(UUID.fromString(nutzerUuid)).isPresent());
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        final String name = "Projekt C";
        assertTrue(projektValueRepository.findById(UUID.fromString(uuid)).isPresent());
        assertTrue(projektValueRepository.findByName(name).isPresent());
        mockMvc.perform(put("/api/projekt/" + uuid)
                .content("{" +
                        "\"name\":\"" + name + "\"," +
                        "\"besitzer\": {\"refId\": \"" + nutzerUuid + "\"}," +
                        "\"allMitglied\": [" +
                        "{\"refId\": \"" + nutzerUuid + "\"}" +
                        "]" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"3\""))
                .andExpect(jsonPath("$.dataId")
                        .isNotEmpty())
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"))
                .andExpect(jsonPath("$.besitzer.refId")
                        .exists())
                .andExpect(jsonPath("$.allMitglied")
                        .isArray())
                .andExpect(jsonPath("$.allMitglied[0]")
                        .exists())
                .andExpect(jsonPath("$.allMitglied[0].refId")
                        .value(nutzerUuid))
                .andExpect(jsonPath("$.allMitglied[1]")
                        .doesNotExist());
    }

    @Test
    @Order(40)
    void getApiProjektById() throws Exception {
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        final String name = "Projekt C";
        assertTrue(projektValueRepository.findById(UUID.fromString(uuid)).isPresent());
        assertTrue(projektValueRepository.findByName(name).isPresent());
        mockMvc.perform(get("/api/projekt/" + uuid)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"3\""))
                .andExpect(jsonPath("$.dataId")
                        .isNotEmpty())
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"))
                .andExpect(jsonPath("$.besitzer.refId")
                        .exists())
                .andExpect(jsonPath("$.allMitglied")
                        .isArray())
                .andExpect(jsonPath("$.allMitglied[0]")
                        .exists())
                .andExpect(jsonPath("$.allMitglied[0].refId")
                        .exists())
                .andExpect(jsonPath("$.allMitglied[1]")
                        .doesNotExist());
    }

    @Test
    @Order(41)
    void getApiProjektByIdNotFound() throws Exception {
        final String uuid = "00000000-6ee8-4335-b12a-ef84794bd27a";
        assertFalse(projektValueRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/projekt/" + uuid)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(42)
    void getApiProjektByName() throws Exception {
        final String name = "Projekt C";
        assertTrue(projektValueRepository.findByName(name).isPresent());
        mockMvc.perform(get("/api/projekt/search/findByName")
                .param("name", name)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"3\""))
                .andExpect(jsonPath("$.dataId")
                        .isNotEmpty())
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"))
                .andExpect(jsonPath("$.besitzer.refId")
                        .exists());
    }

    @Test
    @Order(43)
    void getApiProjektByNameNotFound() throws Exception {
        final String name = "Unbekannt";
        assertFalse(projektValueRepository.findByName(name).isPresent());
        mockMvc.perform(get("/api/projekt/search/findByName")
                .param("name", name)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(50)
    void deleteApiProjekt() throws Exception {
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        assertTrue(projektValueRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(delete("/api/projekt/" + uuid)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNoContent());
    }

    @Test
    @Order(51)
    void deleteApiProjektNotFound() throws Exception {
        final String uuid = "00000000-6ee8-4335-b12a-ef84794bd27a";
        assertFalse(projektValueRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(delete("/api/projekt/" + uuid)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(90)
    void getApiProjekt() throws Exception {
        mockMvc.perform(get("/api/projekt")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(jsonPath("$.content")
                        .isArray())
                .andExpect(jsonPath("$.content[0]")
                        .exists())
                .andExpect(jsonPath("$.content[1]")
                        .exists())
                .andExpect(jsonPath("$.content[2]")
                        .doesNotExist());
        assertEquals(2, projektValueRepository.count());
        projektValueRepository.deleteAll();
        assertEquals(2, nutzerValueRepository.count());
        nutzerValueRepository.deleteAll();
    }
}
