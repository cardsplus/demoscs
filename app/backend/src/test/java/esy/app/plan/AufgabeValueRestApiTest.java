package esy.app.plan;

import esy.api.plan.AufgabeValue;
import esy.api.plan.ProjektValue;
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
import org.springframework.test.context.jdbc.Sql;
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
public class AufgabeValueRestApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AufgabeValueRepository aufgabeValueRepository;

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
    @ValueSource(strings = {"GET", "POST", "PUT", "PATCH", "DELETE"})
    @Order(1)
    void preflight(final String method) throws Exception {
        mockMvc.perform(options("/api/aufgabe")
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
        mockMvc.perform(options("/api/aufgabe")
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

    @Sql("/sql/projekt.sql")
    @Test
    @Order(10)
    void getApiAufgabeNoElement() throws Exception {
        aufgabeValueRepository.deleteAll();
        mockMvc.perform(get("/api/aufgabe")
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
    void postApiAufgabe() throws Exception {
        final ProjektValue projekt = projektValueRepository.findById(UUID.fromString("a1111111-5cc7-3115-a010-de73703ac17f"))
                .orElseThrow();
        final String text = "Aufgabe A";
        assertEquals(0, aufgabeValueRepository.findAll().stream()
                .filter(e -> e.getText().equals(text))
                .count());
        mockMvc.perform(post("/api/aufgabe")
                .content("{" +
                        "\"text\":\"" + text + "\"," +
                        "\"aktiv\": \"true\"," +
                        "\"projekt\": \"/api/projekt/" + projekt.getId() + "\"" +
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
                .andExpect(jsonPath("$.text")
                        .value(text))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"));
        assertEquals(1, aufgabeValueRepository.findAll().stream()
                .filter(e -> e.getText().equals(text))
                .count());
    }

    @Test
    @Order(21)
    void postApiAufgabeAgain() throws Exception {
        final ProjektValue projekt = projektValueRepository.findById(UUID.fromString("a1111111-5cc7-3115-a010-de73703ac17f"))
                .orElseThrow();
        final String text = "Aufgabe A";
        assertEquals(1, aufgabeValueRepository.findAll().stream()
                .filter(e -> e.getText().equals(text))
                .count());
        mockMvc.perform(post("/api/aufgabe")
                .content("{" +
                        "\"text\":\"" + text + "\"," +
                        "\"aktiv\": \"true\"," +
                        "\"projekt\": \"/api/projekt/" + projekt.getId() + "\"" +
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
                .andExpect(jsonPath("$.text")
                        .value(text))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"));
        assertEquals(2, aufgabeValueRepository.findAll().stream()
                .filter(e -> e.getText().equals(text))
                .count());
    }

    @Test
    @Order(22)
    void postApiAufgabeDefault() throws Exception {
        final ProjektValue projekt = projektValueRepository.findById(UUID.fromString("a1111111-5cc7-3115-a010-de73703ac17f"))
                .orElseThrow();
        final String text = "Aufgabe B";
        assertEquals(0, aufgabeValueRepository.findAll().stream()
                .filter(e -> e.getText().equals(text))
                .count());
        mockMvc.perform(post("/api/aufgabe")
                .content("{" +
                        "\"text\":\"" + text + "\"," +
                        "\"projekt\": \"/api/projekt/" + projekt.getId() + "\"" +
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
                .andExpect(jsonPath("$.text")
                        .value(text))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"));
        assertEquals(1, aufgabeValueRepository.findAll().stream()
                .filter(e -> e.getText().equals(text))
                .count());
    }

    @Test
    @Order(30)
    void putApiAufgabe() throws Exception {
        final ProjektValue projekt = projektValueRepository.findById(UUID.fromString("a1111111-5cc7-3115-a010-de73703ac17f"))
                .orElseThrow();
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        final String text = "Aufgabe C";
        assertFalse(aufgabeValueRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(put("/api/aufgabe/" + uuid)
                .content("{" +
                        "\"text\":\"" + text + "\"," +
                        "\"aktiv\": \"true\"," +
                        "\"projekt\": \"/api/projekt/" + projekt.getId() + "\"" +
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
                .andExpect(jsonPath("$.text")
                        .value(text))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"));
        assertTrue(aufgabeValueRepository.findById(UUID.fromString(uuid)).isPresent());
    }

    @RepeatedTest(5)
    @Order(31)
    void putApiAufgabeAgain(final RepetitionInfo info) throws Exception {
        final ProjektValue projekt = projektValueRepository.findById(UUID.fromString("a1111111-5cc7-3115-a010-de73703ac17f"))
                .orElseThrow();
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        final String text = "Aufgabe C";
        assertTrue(aufgabeValueRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(put("/api/aufgabe/" + uuid)
                .content("{" +
                        "\"text\":\"" + text + "\"," +
                        "\"aktiv\": \"true\"," +
                        "\"projekt\": \"/api/projekt/" + projekt.getId() + "\"" +
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
                .andExpect(jsonPath("$.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.text")
                        .value(text))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"));
    }

    @Test
    @Order(32)
    void putApiAufgabeDefault() throws Exception {
        final ProjektValue projekt = projektValueRepository.findById(UUID.fromString("a1111111-5cc7-3115-a010-de73703ac17f"))
                .orElseThrow();
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        final String text = "Aufgabe C";
        assertTrue(aufgabeValueRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(put("/api/aufgabe/" + uuid)
                .content("{" +
                        "\"text\":\"" + text + "\"," +
                        "\"projekt\": \"/api/projekt/" + projekt.getId() + "\"" +
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
                .andExpect(jsonPath("$.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.text")
                        .value(text))
                .andExpect(jsonPath("$.aktiv")
                        .value("true"));
    }

    @Test
    @Order(33)
    void putApiAufgabeAktiv() throws Exception {
        final ProjektValue projekt = projektValueRepository.findById(UUID.fromString("a1111111-5cc7-3115-a010-de73703ac17f"))
                .orElseThrow();
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        final String text = "Aufgabe C";
        assertTrue(aufgabeValueRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(put("/api/aufgabe/" + uuid)
                .content("{" +
                        "\"text\":\"" + text + "\"," +
                        "\"aktiv\": \"false\"," +
                        "\"projekt\": \"/api/projekt/" + projekt.getId() + "\"" +
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
                .andExpect(jsonPath("$.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.text")
                        .value(text))
                .andExpect(jsonPath("$.aktiv")
                        .value("false"));
    }

    @Test
    @Order(34)
    void putApiAufgabeProjekt() throws Exception {
        final ProjektValue projekt = projektValueRepository.findById(UUID.fromString("b2222222-5cc7-3115-a010-de73703ac17f"))
                .orElseThrow();
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        final AufgabeValue aufgabe0 = aufgabeValueRepository.findById(UUID.fromString(uuid))
                .orElseThrow();
        assertNotEquals(projekt.getId(), aufgabe0.getProjekt().getId());
        // https://github.com/spring-projects/spring-data-rest/issues/1426
        mockMvc.perform(put("/api/aufgabe/" + uuid + "/projekt")
                .content("/api/projekt/" + projekt.getId())
                .contentType(MediaType.parseMediaType("text/uri-list"))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNoContent());
        final AufgabeValue aufgabe1 = aufgabeValueRepository.findById(UUID.fromString(uuid))
                .orElseThrow();
        assertEquals(projekt.getId(), aufgabe1.getProjekt().getId());
    }

    @Test
    @Order(35)
    void deleteApiAufgabeProjekt() throws Exception {
        final ProjektValue projekt = projektValueRepository.findById(UUID.fromString("b2222222-5cc7-3115-a010-de73703ac17f"))
                .orElseThrow();
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        final AufgabeValue aufgabe0 = aufgabeValueRepository.findById(UUID.fromString(uuid))
                .orElseThrow();
        assertEquals(projekt.getId(), aufgabe0.getProjekt().getId());
        // https://github.com/spring-projects/spring-data-rest/issues/1426
        mockMvc.perform(delete("/api/aufgabe/" + uuid + "/projekt"))
                .andDo(print())
                .andExpect(status()
                        .isConflict());
        final AufgabeValue aufgabe1 = aufgabeValueRepository.findById(UUID.fromString(uuid))
                .orElseThrow();
        assertEquals(projekt.getId(), aufgabe1.getProjekt().getId());
    }

    @Test
    @Order(40)
    void getApiAufgabeById() throws Exception {
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        final String text = "Aufgabe C";
        assertTrue(aufgabeValueRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/aufgabe/" + uuid)
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
                .andExpect(jsonPath("$.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.text")
                        .value(text))
                .andExpect(jsonPath("$.aktiv")
                        .value("false"));
    }

    @Test
    @Order(41)
    void getApiAufgabeByIdNotFound() throws Exception {
        final String uuid = "00000000-6ee8-4335-b12a-ef84794bd27a";
        assertFalse(aufgabeValueRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/aufgabe/" + uuid)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(50)
    void deleteApiAufgabe() throws Exception {
        final String uuid = "c3333333-3bb4-2113-a010-cd42452ab140";
        assertTrue(aufgabeValueRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(delete("/api/aufgabe/" + uuid))
                .andDo(print())
                .andExpect(status()
                        .isNoContent());
    }

    @Test
    @Order(51)
    void deleteApiAufgabeNotFound() throws Exception {
        final String uuid = "00000000-6ee8-4335-b12a-ef84794bd27a";
        assertFalse(aufgabeValueRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(delete("/api/aufgabe/" + uuid))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(90)
    void getApiAufgabe() throws Exception {
        mockMvc.perform(get("/api/aufgabe")
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
    @Order(99)
    @Transactional
    @Rollback(false)
    void cleanup() {
        assertEquals(3, aufgabeValueRepository.count());
        aufgabeValueRepository.deleteAll();
    }
}
