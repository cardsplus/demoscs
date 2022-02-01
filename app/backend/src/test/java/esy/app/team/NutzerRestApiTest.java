package esy.app.team;

import esy.app.EndpointConfiguration;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
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
@ContextConfiguration(classes = EndpointConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
public class NutzerRestApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NutzerRepository nutzerRepository;

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
    @ValueSource(strings = {"GET", "POST", "PUT", "PATCH", "DELETE"})
    @Order(1)
    void preflight(final String method) throws Exception {
        mockMvc.perform(options("/api/nutzer")
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
    @ValueSource(strings = {"TRACE"})
    @Order(2)
    void preflightNotAllowed(final String method) throws Exception {
        mockMvc.perform(options("/api/nutzer")
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
    void getApiNutzerNoElement() throws Exception {
        nutzerRepository.deleteAll();
        mockMvc.perform(get("/api/nutzer")
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
    void postApiNutzer() throws Exception {
        final String nutzerName = "Mia Musterfrau";
        final String profilName = "Mia";
        final String nutzerMail = nutzerName.replace(' ', '.') + "@a.de";
        assertFalse(nutzerRepository.findByMail(nutzerMail).isPresent());
        mockMvc.perform(post("/api/nutzer")
                .content("{" +
                        "\"mail\":\"" + nutzerMail + "\"," +
                        "\"name\":\"" + nutzerName + "\"," +
                        "\"aktiv\": \"true\"," +
                        "\"allSprache\": [\"EN\"]" +
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
                        .string("ETag", "\"1\""))
                .andExpect(jsonPath("$.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.mail")
                        .value(nutzerMail))
                .andExpect(jsonPath("$.name")
                        .value(nutzerName))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"))
                .andExpect(jsonPath("$.allSprache")
                        .isArray())
                .andExpect(jsonPath("$.allSprache[0]")
                        .value("EN"))
                .andExpect(jsonPath("$.allSprache[1]")
                        .doesNotExist());
        assertTrue(nutzerRepository.findByMail(nutzerMail).isPresent());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @Order(21)
    void postApiNutzerConflict(final boolean aktiv) throws Exception {
        final String nutzerName = "Mia Musterfrau";
        final String nutzerMail = nutzerName.replace(' ', '.') + "@a.de";
        assertTrue(nutzerRepository.findByMail(nutzerMail).isPresent());
        mockMvc.perform(post("/api/nutzer")
                .content("{" +
                        "\"mail\":\"" + nutzerMail + "\"," +
                        "\"name\":\"" + nutzerName + "\"," +
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
    void postApiNutzerDefault() throws Exception {
        final String nutzerName = "Bea Musterfrau";
        final String nutzerMail = nutzerName.replace(' ', '.') + "@a.de";
        assertFalse(nutzerRepository.findByMail(nutzerMail).isPresent());
        mockMvc.perform(post("/api/nutzer")
                .content("{" +
                        "\"mail\":\"" + nutzerMail + "\"," +
                        "\"name\":\"" + nutzerName + "\"" +
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
                .andExpect(jsonPath("$.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.mail")
                        .value(nutzerMail))
                .andExpect(jsonPath("$.name")
                        .value(nutzerName))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"))
                .andExpect(jsonPath("$.allSprache")
                        .isArray())
                .andExpect(jsonPath("$.allSprache[0]")
                        .doesNotExist());
        assertTrue(nutzerRepository.findByMail(nutzerMail).isPresent());
    }

    @Test
    @Order(30)
    void putApiNutzer() throws Exception {
        final String nutzerUuid = "a1111111-6ee8-4335-b12a-ef84794bd27a";
        final String profilName = "Max";
        final String nutzerName = "Max Mustermann";
        final String nutzerMail = nutzerName.replace(' ', '.') + "@a.de";
        assertFalse(nutzerRepository.findById(UUID.fromString(nutzerUuid)).isPresent());
        assertFalse(nutzerRepository.findByMail(nutzerMail).isPresent());
        mockMvc.perform(put("/api/nutzer/" + nutzerUuid)
                .content("{" +
                        "\"mail\":\"" + nutzerMail + "\"," +
                        "\"name\":\"" + nutzerName + "\"," +
                        "\"aktiv\": \"true\"," +
                        "\"allSprache\": [\"DE\", \"EN\"]" +
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
                        .string("ETag", "\"1\""))
                .andExpect(jsonPath("$.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.mail")
                        .value(nutzerMail))
                .andExpect(jsonPath("$.name")
                        .value(nutzerName))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"))
                .andExpect(jsonPath("$.allSprache")
                        .isArray())
                .andExpect(jsonPath("$.allSprache[0]")
                        .value("DE"))
                .andExpect(jsonPath("$.allSprache[1]")
                        .value("EN"))
                .andExpect(jsonPath("$.allSprache[2]")
                        .doesNotExist());
        assertTrue(nutzerRepository.findByMail(nutzerMail).isPresent());
    }

    @RepeatedTest(5)
    @Order(31)
    void putApiNutzerAgain(final RepetitionInfo info) throws Exception {
        final String nutzerUuid = "a1111111-6ee8-4335-b12a-ef84794bd27a";
        final String nutzerName = "Max Mustermann";
        final String nutzerMail = nutzerName.replace(' ', '.') + "@a.de";
        assertTrue(nutzerRepository.findById(UUID.fromString(nutzerUuid)).isPresent());
        assertTrue(nutzerRepository.findByMail(nutzerMail).isPresent());
        mockMvc.perform(put("/api/nutzer/" + nutzerUuid)
                .content("{" +
                        "\"mail\":\"" + nutzerMail + "\"," +
                        "\"name\":\"" + nutzerName + "\"," +
                        "\"aktiv\": \"true\"," +
                        "\"allSprache\": [\"DE\", \"EN\"]" +
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
                        .string("ETag", "\"" + (info.getCurrentRepetition()+1) + "\""))
                .andExpect(jsonPath("$.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.mail")
                        .value(nutzerMail))
                .andExpect(jsonPath("$.name")
                        .value(nutzerName))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"))
                .andExpect(jsonPath("$.allSprache")
                        .isArray())
                .andExpect(jsonPath("$.allSprache[0]")
                        .value("DE"))
                .andExpect(jsonPath("$.allSprache[1]")
                        .value("EN"))
                .andExpect(jsonPath("$.allSprache[2]")
                        .doesNotExist());
    }

    @Test
    @Order(32)
    void putApiNutzerDefault() throws Exception {
        final String nutzerUuid = "a1111111-6ee8-4335-b12a-ef84794bd27a";
        final String nutzerName = "Max Mustermann";
        final String nutzerMail = nutzerName.replace(' ', '.') + "@a.de";
        assertTrue(nutzerRepository.findById(UUID.fromString(nutzerUuid)).isPresent());
        assertTrue(nutzerRepository.findByMail(nutzerMail).isPresent());
        mockMvc.perform(put("/api/nutzer/" + nutzerUuid)
                .content("{" +
                        "\"mail\":\"" + nutzerMail + "\"," +
                        "\"name\":\"" + nutzerName + "\"" +
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
                        .string("ETag", "\"7\""))
                .andExpect(jsonPath("$.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.mail")
                        .value(nutzerMail))
                .andExpect(jsonPath("$.name")
                        .value(nutzerName))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"))
                .andExpect(jsonPath("$.allSprache")
                        .isArray())
                .andExpect(jsonPath("$.allSprache[0]")
                        .doesNotExist());
    }

    @Test
    @Order(34)
    void putApiNutzerAktiv() throws Exception {
        final String nutzerUuid = "a1111111-6ee8-4335-b12a-ef84794bd27a";
        final String nutzerName = "Max Mustermann";
        final String nutzerMail = nutzerName.replace(' ', '.') + "@a.de";
        assertTrue(nutzerRepository.findById(UUID.fromString(nutzerUuid)).isPresent());
        assertTrue(nutzerRepository.findByMail(nutzerMail).isPresent());
        mockMvc.perform(put("/api/nutzer/" + nutzerUuid)
                .content("{" +
                        "\"mail\":\"" + nutzerMail + "\"," +
                        "\"name\":\"" + nutzerName + "\"," +
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
                        .string("ETag", "\"8\""))
                .andExpect(jsonPath("$.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.mail")
                        .value(nutzerMail))
                .andExpect(jsonPath("$.name")
                        .value(nutzerName))
                .andExpect(jsonPath("$.aktiv")
                        .value("false"))
                .andExpect(jsonPath("$.allSprache")
                        .isArray())
                .andExpect(jsonPath("$.allSprache[0]")
                        .doesNotExist());
    }

    @Test
    @Order(40)
    void getApiNutzerById() throws Exception {
        final String nutzerUuid = "a1111111-6ee8-4335-b12a-ef84794bd27a";
        final String nutzerName = "Max Mustermann";
        final String nutzerMail = nutzerName.replace(' ', '.') + "@a.de";
        assertTrue(nutzerRepository.findById(UUID.fromString(nutzerUuid)).isPresent());
        assertTrue(nutzerRepository.findByMail(nutzerMail).isPresent());
        mockMvc.perform(get("/api/nutzer/" + nutzerUuid)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"8\""))
                .andExpect(jsonPath("$.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.mail")
                        .value(nutzerMail))
                .andExpect(jsonPath("$.name")
                        .value(nutzerName))
                .andExpect(jsonPath("$.aktiv")
                        .value("false"))
                .andExpect(jsonPath("$.allSprache")
                        .isArray())
                .andExpect(jsonPath("$.allSprache[0]")
                        .doesNotExist());
    }

    @Test
    @Order(41)
    void getApiNutzerByIdNotFound() throws Exception {
        final String nutzerUuid = "00000000-6ee8-4335-b12a-ef84794bd27a";
        assertFalse(nutzerRepository.findById(UUID.fromString(nutzerUuid)).isPresent());
        mockMvc.perform(get("/api/nutzer/" + nutzerUuid)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(42)
    void getApiNutzerByMail() throws Exception {
        final String nutzerName = "Max Mustermann";
        final String nutzerMail = nutzerName.replace(' ', '.') + "@a.de";
        assertTrue(nutzerRepository.findByMail(nutzerMail).isPresent());
        mockMvc.perform(get("/api/nutzer/search/findByMail")
                .param("mail", nutzerMail)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"8\""))
                .andExpect(jsonPath("$.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.mail")
                        .value(nutzerMail))
                .andExpect(jsonPath("$.name")
                        .value(nutzerName))
                .andExpect(jsonPath("$.aktiv")
                        .value("false"))
                .andExpect(jsonPath("$.allSprache")
                        .isArray())
                .andExpect(jsonPath("$.allSprache[0]")
                        .doesNotExist());
    }

    @Test
    @Order(43)
    void getApiNutzerByMailNotFound() throws Exception {
        final String nutzerName = "Unbekannt";
        final String nutzerMail = nutzerName.replace(' ', '.') + "@a.de";
        assertFalse(nutzerRepository.findByMail(nutzerMail).isPresent());
        mockMvc.perform(get("/api/nutzer/search/findByMail")
                .param("mail", nutzerMail)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(44)
    void getApiNutzerItem() throws Exception {
        assertEquals(3, nutzerRepository.findAll().size());
        mockMvc.perform(get("/api/nutzer/search/findAllItem")
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
                        .exists())
                .andExpect(jsonPath("$.content[3]")
                        .doesNotExist());
    }

    @Test
    @Order(50)
    void deleteApiNutzer() throws Exception {
        final String nutzerUuid = "a1111111-6ee8-4335-b12a-ef84794bd27a";
        assertTrue(nutzerRepository.findById(UUID.fromString(nutzerUuid)).isPresent());
        mockMvc.perform(delete("/api/nutzer/" + nutzerUuid)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNoContent());
    }

    @Test
    @Order(51)
    void deleteApiNutzerNotFound() throws Exception {
        final String nutzerUuid = "00000000-6ee8-4335-b12a-ef84794bd27a";
        assertFalse(nutzerRepository.findById(UUID.fromString(nutzerUuid)).isPresent());
        mockMvc.perform(delete("/api/nutzer/" + nutzerUuid)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(90)
    void getApiNutzer() throws Exception {
        mockMvc.perform(get("/api/nutzer")
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
    }

    @Test
    @Order(99)
    @Transactional
    @Rollback(false)
    void cleanup() {
        assertEquals(2, nutzerRepository.count());
        nutzerRepository.deleteAll();
    }
}
