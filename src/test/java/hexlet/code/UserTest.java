package hexlet.code;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.Optional;

import hexlet.code.dto.User.UserUpdateDTO;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
public class UserTest {

    @Autowired
    private Faker faker;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    private JwtRequestPostProcessor token;

    private User testUser;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@Example.com"));
        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser);
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
    public void testCreate() throws Exception {
        var data = Map.of(
                "email", faker.internet().emailAddress(),
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName(),
                "password", faker.internet().password(3, 12)
        );

        var request = post("/api/users").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(data.get("email"))
                .orElse(null);

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(data.get("email"));
        assertThat(user.getFirstName()).isEqualTo(data.get("firstName"));
        assertThat(user.getLastName()).isEqualTo(data.get("lastName"));
        assertThat(user.getPassword()).isEqualTo(data.get("password"));
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
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedUser = userRepository.findById(testUser.getId()).orElse(null);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo(data.get("email"));
        assertThat(updatedUser.getFirstName()).isEqualTo(data.get("firstName"));
        assertThat(updatedUser.getLastName()).isEqualTo(data.get("lastName"));
        assertThat(updatedUser.getPasswordDigest()).isEqualTo(data.get("password"));
    }

    @Test
    public void testDestroy() throws Exception {

        userRepository.save(testUser);

        var request = delete("/api/users/{id}", testUser.getId()).with(token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        Optional<User> userOptional = userRepository.findById(testUser.getId());
        assertThat(userOptional.isEmpty()).isEqualTo(true);
    }
}
