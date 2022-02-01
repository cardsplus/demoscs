package scs.app.owner;

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
class PetRestApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PetRepository petRepository;

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

    @Sql("/sql/owner.sql")
    @Test
    @Order(10)
    void getApiPetNoElement() throws Exception {
        assertEquals(0, petRepository.count());
        mockMvc.perform(get("/api/pet")
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
    void postApiPet() throws Exception {
        final String name = "Roger";
        assertFalse(petRepository.findByName(name).isPresent());
        mockMvc.perform(post("/api/pet")
                        .content("{" +
                                "\"owner\": \"/api/owner/b1111111-1111-beef-dead-beefdeadbeef\"," +
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
                .andExpect(jsonPath("$.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.name")
                        .value(name));
    }

    @Test
    @Order(21)
    void postApiPetConflict() throws Exception {
        final String name = "Roger";
        assertTrue(petRepository.findByName(name).isPresent());
        mockMvc.perform(post("/api/pet")
                        .content("{" +
                                "\"owner\": \"/api/owner/b1111111-1111-beef-dead-beefdeadbeef\"," +
                                "\"name\":\"" + name + "\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isConflict());
    }

    @Test
    @Order(30)
    void putApiPet() throws Exception {
        final String name = "Anita";
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertFalse(petRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(put("/api/pet/" + uuid)
                        .content("{" +
                                "\"owner\": \"/api/owner/b1111111-1111-beef-dead-beefdeadbeef\"," +
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
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.ownerItem.value")
                        .value("b1111111-1111-beef-dead-beefdeadbeef"))
                .andExpect(jsonPath("$.ownerItem.text")
                        .value("Toby Elsden"))
                .andExpect(jsonPath("$.name")
                        .value(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Atina",
            "Anita"
    })
    @Order(31)
    void patchApiPetName(final String name) throws Exception {
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(petRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(patch("/api/pet/" + uuid)
                        .content("{" +
                                "\"name\": \"" + name + "\"" +
                                "}")
                        .contentType(MediaType.parseMediaType("application/merge-patch+json"))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .exists("ETag"))
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.ownerItem.value")
                        .value("b1111111-1111-beef-dead-beefdeadbeef"))
                .andExpect(jsonPath("$.ownerItem.text")
                        .value("Toby Elsden"))
                .andExpect(jsonPath("$.name")
                        .value(name));
    }

    @Test
    @Order(32)
    void patchApiPetOwner() throws Exception {
        final String name = "Anita";
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(petRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(patch("/api/pet/" + uuid)
                        .content("{" +
                                "\"owner\": \"/api/owner/b2222222-2222-beef-dead-beefdeadbeef\"" +
                                "}")
                        .contentType(MediaType.parseMediaType("application/merge-patch+json"))
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
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.ownerItem.value")
                        .value("b2222222-2222-beef-dead-beefdeadbeef"))
                .andExpect(jsonPath("$.ownerItem.text")
                        .value("Emma Milner"))
                .andExpect(jsonPath("$.name")
                        .value(name));
    }

    @Test
    @Order(33)
    void getApiPetOwner() throws Exception {
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(petRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/pet/" + uuid + "/owner")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(jsonPath("$.id")
                        .value("b2222222-2222-beef-dead-beefdeadbeef"))
                .andExpect(jsonPath("$.name")
                        .value("Emma Milner"));
    }

    @Test
    @Order(40)
    void getApiPet() throws Exception {
        assertEquals(2, petRepository.count());
        mockMvc.perform(get("/api/pet/search/findAllByOrderByNameAsc")
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
    @Order(41)
    void getApiPetById() throws Exception {
        final String name = "Anita";
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(petRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/pet/" + uuid)
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
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.name")
                        .value(name));
    }

    @Test
    @Order(42)
    void getApiPetByIdNotFound() throws Exception {
        final String uuid = "a1111111-ffff-beef-dead-beefdeadbeef";
        assertFalse(petRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/pet/" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(43)
    void getApiPetByName() throws Exception {
        final String name = "Anita";
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        mockMvc.perform(get("/api/pet/search/findByName")
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
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.name")
                        .value(name));
    }

    @Test
    @Order(44)
    void getApiPetByNameNotFound() throws Exception {
        final String name = "Atina";
        assertFalse(petRepository.findByName(name).isPresent());
        mockMvc.perform(get("/api/pet/search/findByName")
                        .param("name", name)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(50)
    void deleteApiPet() throws Exception {
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(petRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(delete("/api/pet/" + uuid))
                .andDo(print())
                .andExpect(status()
                        .isNoContent());
    }

    @Test
    @Order(51)
    void deleteApiPetNotFound() throws Exception {
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertFalse(petRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(delete("/api/pet/" + uuid))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(99)
    @Transactional
    @Rollback(false)
    void cleanup() {
        assertEquals(1, petRepository.count());
        petRepository.deleteAll();
        assertEquals(2, ownerRepository.count());
        ownerRepository.deleteAll();
    }
}