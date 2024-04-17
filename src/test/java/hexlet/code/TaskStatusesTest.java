package hexlet.code;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusesTest {

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
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        var dto = taskStatusMapper.map(testTaskStatus);

        var request = post("/api/task_statuses").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var taskStatuses = taskStatusRepository.findBySlug(testTaskStatus.getSlug()).orElse(null);

        assertThat(taskStatuses).isNotNull();
        assertThat(taskStatuses.getName()).isEqualTo(testTaskStatus.getName());
        assertThat(taskStatuses.getSlug()).isEqualTo(testTaskStatus.getSlug());
    }

    @Test
    public void testUpdate() throws Exception {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        taskStatusRepository.save(testTaskStatus);

        var data = new TaskStatusUpdateDTO(JsonNullable.of(testTaskStatus.getName()), JsonNullable.of(testTaskStatus.getSlug()));

        var request = put("/api/task_statuses/{id}", testTaskStatus.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedTaskStatus = taskStatusRepository.findById(testTaskStatus.getId()).orElse(null);

        assertThat(updatedTaskStatus).isNotNull();
        assertThat(updatedTaskStatus.getName()).isEqualTo(data.getName());
        assertThat(updatedTaskStatus.getSlug()).isEqualTo(data.getSlug());
    }

    @Test
    public void testDestroy() throws Exception {

        taskStatusRepository.save(testTaskStatus);

        var request = delete("/task_statuses/{id}", testTaskStatus.getId()).with(token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.existsById(testTaskStatus.getId())).isEqualTo(true);
    }
}
