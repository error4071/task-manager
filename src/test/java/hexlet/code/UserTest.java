package hexlet.code;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import hexlet.code.dto.User.UserCreateDTO;
import hexlet.code.dto.User.UserUpdateDTO;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
    private static final Faker FAKER = new Faker();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
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
        UserCreateDTO userCreateDTO = new UserCreateDTO();

                userCreateDTO.setEmail(FAKER.internet().emailAddress());
                userCreateDTO.setFirstName(FAKER.name().firstName());
                userCreateDTO.setLastName(FAKER.name().lastName());
                userCreateDTO.setPassword(FAKER.internet().password(3, 12));

                String data = objectMapper.writeValueAsString(userCreateDTO);

        MockHttpServletRequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(data)
                .with(token);

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Optional<User> userOptional = userRepository.findByEmail(userCreateDTO.getEmail());

        assertThat(userOptional.isPresent()).isTrue();

        User user = userOptional.get();

        assertThat(user.getEmail()).isEqualTo(userCreateDTO.getEmail());
        assertThat(user.getFirstName()).isEqualTo(userCreateDTO.getFirstName());
        assertThat(user.getLastName()).isEqualTo(userCreateDTO.getLastName());
        assertThat(user.getPasswordDigest()).isEqualTo(userCreateDTO.getPassword());
    }

    @Test
    public void testUpdate() throws Exception {
        userRepository.save(testUser);
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();

                userUpdateDTO.setEmail(JsonNullable.of(FAKER.internet().emailAddress()));
                userUpdateDTO.setPassword(JsonNullable.of(FAKER.internet().password(3, 12)));

        MockHttpServletRequestBuilder request = put("/api/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDTO))
                .with(SecurityMockMvcRequestPostProcessors.user(testUser.getUsername()));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Optional<User> updatedUser = userRepository.findById(testUser.getId());

        assertThat(updatedUser.isPresent()).isTrue();

        User user = updatedUser.get();

        assertThat(user.getEmail()).isEqualTo(userUpdateDTO.getEmail().get());
        assertThat(user.getPasswordDigest()).isEqualTo(userUpdateDTO.getPassword().get());
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
