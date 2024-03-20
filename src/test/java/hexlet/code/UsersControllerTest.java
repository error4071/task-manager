package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;

import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.util.Map;


import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;





    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private User testUser;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser);
    }



    @Test
    public void testShow() throws Exception {
        var createdAt = java.util.Date.from(testUser.getCreatedAt().atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant());

        var request = get("/api/users/{id}", testUser.getId()).with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("createdAt").isEqualTo(createdAt)
        );
    }

    @Test
    public void testShowUserNotFound() throws Exception {
        var jwt = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
        mockMvc.perform(delete("/api/users/{id}", testUser.getId()).with(jwt));

        var request = get("/api/users/{id}", testUser.getId()).with(jwt);
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testShowWithoutAuth() throws Exception {
        var request = get("/api/users/{id}", testUser.getId());
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/users").with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        assertThat(body).contains(String.valueOf(testUser.getId()));
        assertThat(body).contains(testUser.getEmail());
        assertThat(body).contains(testUser.getFirstName());
        assertThat(body).contains(testUser.getLastName());
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        var request = get("/api/users");
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreate() throws Exception {
        var data = Map.of(
                "email", faker.internet().emailAddress(),
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName(),
                "password", faker.internet().password(3, 12)
        );


        var request = post("/api/users").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(data.get("email")).orElse(null);

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(data.get("email"));
        assertThat(user.getFirstName()).isEqualTo(data.get("firstName"));
        assertThat(user.getLastName()).isEqualTo(data.get("lastName"));
        assertThat(user.getPasswordDigest()).isNotEqualTo(data.get("password"));
    }

    @Test
    public void testCreateWithoutAuth() throws Exception {
        var data = Map.of(
                "email", faker.internet().emailAddress(),
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName(),
                "password", faker.internet().password(3, 12)
        );

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreateWithoutFirstNameAndLastName() throws Exception {
        var dataWithoutFirstNameAndLastName = Map.of(
                "email", faker.internet().emailAddress(),
                "password", faker.internet().password(3, 12)
        );

        var request = post("/api/users").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dataWithoutFirstNameAndLastName));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(dataWithoutFirstNameAndLastName.get("email")).orElse(null);

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(dataWithoutFirstNameAndLastName.get("email"));
        assertThat(user.getPasswordDigest()).isNotEqualTo(dataWithoutFirstNameAndLastName.get("password"));
    }

    @Test
    public void testCreateWithInvalidPassword() throws Exception {
        var dataWithInvalidPassword = Map.of(
                "email", faker.internet().emailAddress(),
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName(),
                "password", faker.internet().password(1, 2)
        );

        var request = post("/api/users").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dataWithInvalidPassword));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateWithInvalidEmail() throws Exception {
        var dataWithInvalidEmail = Map.of(
                "email", faker.name().username(),
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName(),
                "password", faker.internet().password(3, 12)
        );

        var request = post("/api/users").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dataWithInvalidEmail));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate() throws Exception {
        var data = Map.of(
                "email", faker.internet().emailAddress(),
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName(),
                "password", faker.internet().password(3, 12)
        );

        var jwt = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
        var request = put("/api/users/{id}", testUser.getId()).with(jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedUser = userRepository.findById(testUser.getId()).orElse(null);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo(data.get("email"));
        assertThat(updatedUser.getFirstName()).isEqualTo(data.get("firstName"));
        assertThat(updatedUser.getLastName()).isEqualTo(data.get("lastName"));
        assertThat(updatedUser.getPasswordDigest()).isNotEqualTo(data.get("password"));
    }

    @Test
    public void testPartialUpdate() throws Exception {
        var data = Map.of(
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName()
        );

        var jwt = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
        var request = put("/api/users/{id}", testUser.getId()).with(jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedUser = userRepository.findById(testUser.getId()).orElse(null);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(updatedUser.getFirstName()).isEqualTo(data.get("firstName"));
        assertThat(updatedUser.getLastName()).isEqualTo(data.get("lastName"));
        assertThat(updatedUser.getPassword()).isEqualTo(testUser.getPassword());

    }

    @Test
    public void testUpdateWithoutAuth() throws Exception {
        var data = Map.of(
                "email", faker.internet().emailAddress(),
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName(),
                "passwordDigest", faker.internet().password(3, 12)
        );

        var request = put("/api/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDestroy() throws Exception {
        var jwt = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
        var request = delete("/api/users/{id}", testUser.getId()).with(jwt);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        var user = userRepository.findById(testUser.getId()).orElse(null);
        assertThat(user).isNull();
    }

    @Test
    public void testDestroyWithoutAuth() throws Exception {
        var request = delete("/api/users/{id}", testUser.getId());
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());

    }
}