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
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.instancio.Select;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;
import java.util.Optional;

@ContextConfiguration(classes = AppApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TaskStatusesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ModelGenerator modelGenerator = new ModelGenerator();

    private TaskStatus testTaskStatus;

    private TaskStatusMapper taskStatusMapper;

    @BeforeEach
    public void beforeEach() {
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
        taskStatusRepository.save(testTaskStatus);
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/task_statuses").with(jwt());
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

        taskStatusCreateDTO.setName(faker.internet().emailAddress());
        taskStatusCreateDTO.setSlug(faker.internet().slug());

        var request = post("/api/task_statuses")
                .with(jwt())
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

        jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        testTaskStatus = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName), () -> "To experiment")
                .supply(Select.field(TaskStatus::getSlug), () -> "to_experiment")
                .create();

        taskStatusRepository.save(testTaskStatus);

        var taskStatusData = Map.of(
                "name", "To test update",
                "slug", "to_test_update"
        );

        MockHttpServletRequestBuilder request = put("/api/task_statuses/{id}", testTaskStatus.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatusData));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedTaskStatus = taskStatusRepository.findById(testTaskStatus.getId()).orElse(null);

        assertThat(updatedTaskStatus).isNotNull();
        assertThat(updatedTaskStatus.getName()).isEqualTo(taskStatusData.get("name"));
        assertThat(updatedTaskStatus.getSlug()).isEqualTo(taskStatusData.get("slug"));
    }

    @Test
    public void testDestroy() throws Exception {

        var request = delete("/api/task_statuses/{id}", testTaskStatus.getId()).with(jwt());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        Optional<TaskStatus> taskStatusOptional = taskStatusRepository.findBySlug(testTaskStatus.getSlug());

        assertThat(taskStatusOptional.isEmpty()).isEqualTo(true);
    }
}
