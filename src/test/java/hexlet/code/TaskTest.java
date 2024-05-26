package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.Task.TaskUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class TaskTest {

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

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private Task testTask;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));

        var user = userRepository.findByEmail("hexlet@example.com")
                .orElseThrow(() -> new RuntimeException("User doesn't exist"));

        var taskStatus = taskStatusRepository.findBySlug("draft")
                .orElseThrow(() -> new RuntimeException("TaskStatus doesn't exist"));

        var label = labelRepository.findByName("feature")
                .orElseThrow(() -> new RuntimeException("Label doesn't exist"));

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setAssignee(user);
        testTask.setTaskStatus(taskStatus);
        testTask.setLabels(Set.of(label));
        taskRepository.save(testTask);
    }

    @AfterEach
    public void cleanUp() {
        taskRepository.deleteById(testTask.getId());
    }

    @Test
    public void testShow() throws Exception {
        var createdAt = java.util.Date.from(testTask.getCreatedAt()
                .atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        var request = get("/api/tasks/{id}", testTask.getId()).with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("index").isEqualTo(testTask.getIndex()),
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug()),
                v -> v.node("createdAt").isEqualTo(createdAt)
        );
    }

    @Test
    public void testCreate() throws Exception {
        var data = Map.of(
                "index", (Integer) faker.number().positive(),
                "assignee_id", 1L,
                "title", faker.lorem().word(),
                "content", faker.lorem().sentence(),
                "status", "draft",
                "taskLabelIds", List.of(1L)
        );

        var request = post("/api/tasks").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var task = taskRepository.findByName((String) data.get("title")).orElse(null);

        assertThat(task).isNotNull();
        assertThat(task.getIndex()).isEqualTo(data.get("index"));
        assertThat(task.getName()).isEqualTo(data.get("title"));
        assertThat(task.getDescription()).isEqualTo(data.get("content"));
        assertThat(task.getTaskStatus().getName()).isEqualTo("Draft");
        assertThat(task.getAssignee().getId()).isEqualTo(data.get("assignee_id"));
        assertThat(task.getLabels().iterator().next().getId()).isEqualTo(1L);
    }

    @Test
    public void testUpdate() throws Exception {
        var user = userRepository.findByEmail("hexlet@example.com").orElseThrow();

        var data = new TaskUpdateDTO();
        data.setIndex(JsonNullable.of(faker.number().positive()));
        data.setAssigneeId(JsonNullable.of(user.getId()));
        data.setTitle(JsonNullable.of(faker.lorem().word()));
        data.setContent(JsonNullable.of(faker.lorem().sentence()));
        data.setStatus(JsonNullable.of("published"));

        var request = put("/api/tasks/{id}", testTask.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedTask = taskRepository.findById(testTask.getId()).orElse(null);

        assertThat(updatedTask).isNotNull();
        assertThat(updatedTask.getIndex()).isEqualTo(data.getIndex().get());
        assertThat(updatedTask.getAssignee().getId()).isEqualTo(data.getAssigneeId().get());
        assertThat(updatedTask.getName()).isEqualTo(data.getTitle().get());
        assertThat(updatedTask.getDescription()).isEqualTo(data.getContent().get());
        assertThat(updatedTask.getTaskStatus().getSlug()).isEqualTo(data.getStatus().get());
        assertThat(updatedTask.getLabels().iterator().next().getId()).isEqualTo(2L);
    }

    @Test
    public void testDestroy() throws Exception {
        var request = delete("/api/tasks/{id}", testTask.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        var task = taskRepository.findById(testTask.getId()).orElse(null);
        assertThat(task).isNull();
    }
}