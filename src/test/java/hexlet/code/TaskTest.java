package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
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
import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private Task testTask;

    private TaskMapper taskMapper;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@Example.com"));

        var userTest = userRepository.findByEmail("hexlet@Example.com")
                .orElseThrow(() -> new RuntimeException("User not found."));

        var taskStatusTest = taskStatusRepository.findBySlug("draft")
                .orElseThrow(() -> new RuntimeException("TaskStatus not found."));

        var labelTest = labelRepository.findByName("feature")
                .orElseThrow(() -> new RuntimeException("Label not found."));

        testTask = Instancio.of(modelGenerator.getTaskModel())
                .create();
        testTask.setAssignee(userTest);
        testTask.setTaskStatus(taskStatusTest);
        taskRepository.save(testTask);
    }

    @Test
    public void testCreate() throws Exception {
        var data = Map.of(
                "index", (Integer) faker.number().positive(),
                "assignee_id", 1L,
                "title", faker.lorem().word(),
                "content", faker.lorem().sentence(),
                "status", "draft"
        );

        var request = post("/api/tasks").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var task = taskRepository.findByName((String) data.get("title")).orElse(null);

        assertThat(task).isNotNull();
        assertThat(task.getIndex()).isEqualTo(data.get("index"));
        assertThat(task.getAssignee().getId()).isEqualTo(data.get("assignee_id"));
        assertThat(task.getName()).isEqualTo(data.get("title"));
        assertThat(task.getDescription()).isEqualTo(data.get("content"));
        assertThat(task.getTaskStatus()).isEqualTo(data.get("draft"));
    }
}
