package hexlet.code;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
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

import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusesTest {

    private static final Faker FAKER = new Faker();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    private JwtRequestPostProcessor token;

    private TaskStatus testTaskStatus;

    private TaskStatusMapper taskStatusMapper;


    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@Example.com"));
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
        taskStatusRepository.save(testTaskStatus);
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/task_statuses").with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        assertThat(body).contains(String.valueOf(testTaskStatus.getId()));
        assertThat(body).contains(testTaskStatus.getName());
        assertThat(body).contains(testTaskStatus.getSlug());
    }

    @Test
    public void testCreate() throws Exception {
        TaskStatusCreateDTO taskStatusCreateDTO = new TaskStatusCreateDTO();

        taskStatusCreateDTO.setName(FAKER.internet().emailAddress());
        taskStatusCreateDTO.setSlug(FAKER.internet().slug());

        var request = post("/api/task_statuses")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatusCreateDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var taskStatusTest = taskStatusRepository.findBySlug(taskStatusCreateDTO.getSlug()).get();

        assertThat(taskStatusTest).isNotNull();
        assertThat(taskStatusTest.getName()).isEqualTo(taskStatusCreateDTO.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        TaskStatusUpdateDTO taskStatusUpdateDTO = new TaskStatusUpdateDTO();

            taskStatusUpdateDTO.setName(JsonNullable.of(faker.internet().emailAddress()));
            taskStatusUpdateDTO.setSlug(JsonNullable.of(faker.internet().slug()));

        MockHttpServletRequestBuilder request = put("/api/labels/{id}", testTaskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(objectMapper.writeValueAsString(taskStatusUpdateDTO))
                .with(SecurityMockMvcRequestPostProcessors.user(testTaskStatus.getName()));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedTaskStatus = taskStatusRepository.findById(testTaskStatus.getId()).get();

        assertThat(updatedTaskStatus).isNotNull();
        assertThat(updatedTaskStatus.getName()).isEqualTo(taskStatusUpdateDTO.getName().get());
        assertThat(updatedTaskStatus.getSlug()).isEqualTo(taskStatusUpdateDTO.getSlug().get());
    }

    @Test
    public void testDestroy() throws Exception {

        var request = delete("/api/task_statuses/{id}", testTaskStatus.getId()).with(token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        Optional<TaskStatus> taskStatusOptional = taskStatusRepository.findBySlug(testTaskStatus.getSlug());

        assertThat(taskStatusOptional.isEmpty()).isEqualTo(true);
    }
}
