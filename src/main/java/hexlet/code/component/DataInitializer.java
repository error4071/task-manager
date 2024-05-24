package hexlet.code.component;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.User.UserCreateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.ModelGenerator;
import lombok.AllArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.apache.commons.text.*;

import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;

    private final TaskStatusRepository taskStatusRepository;

    private final LabelRepository labelRepository;

    private final UserMapper userMapper;

    private final TaskStatusMapper taskStatusMapper;

    private final LabelMapper labelMapper;

    private PasswordEncoder passwordEncoder;

    private TaskRepository taskRepository;

    private Task task;

    private ModelGenerator modelGenerator;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        addModels();
    }

    private void addModels() {
        var userData = new UserCreateDTO();
        userData.setEmail("hexlet@example.com");
        userData.setPassword("qwerty");
        var user = userMapper.map(userData);
        userRepository.save(user);

        var labelNames = Arrays.asList("bug", "feature");
        labelNames.stream()
                .map(name -> {
                    var label = new Label();
                    label.setName(name);
                    labelRepository.save(label);
                    return label;
                }).toList();

        var statusNames = Arrays.asList("draft", "to_review", "to_be_fixed", "to_publish", "published");
        var taskStatuses = statusNames.stream()
                .map(name -> {
                    var taskStatus = new TaskStatus();
                    taskStatus.setSlug(name);
                    taskStatus.setName(CaseUtils.toCamelCase(name, true, new char[] { '_' }));
                    taskStatusRepository.save(taskStatus);
                    return taskStatus;
                }).toList();

        var tasks = Instancio.ofList(modelGenerator.getTaskModel())
                .size(10)
                .generate(Select.field(Task::getTaskStatus), gen -> gen.oneOf(taskStatuses))
                .supply(Select.field(Task::getAssignee), () -> user)
                .create();
        taskRepository.saveAll(tasks);
    }

    public void addSlug() {
        List<String> defaultSlug = List.of("draft", "to_review", "to_be_fixed", "to_publish", "published");
        defaultSlug.forEach(slug -> {
            var statusData = new TaskStatusCreateDTO();
            String[] arr = slug.split("_");
            String data = arr[0].substring(0, 1).toUpperCase() + arr[0].substring(1);
            var name = new StringBuilder(String.valueOf(data));

            if (arr.length > 1) {
                for (var element: arr) {
                    name.append(" ").append(element);
                }
            }

            statusData.setName(name.toString());
            statusData.setSlug(slug.toString());
            var status = taskStatusMapper.map(statusData);
            taskStatusRepository.save(status);
        });
    }

    public void addLabel() {
        List<String> labels = List.of("feature", "bug");
        labels.forEach(name -> {
            var labelData = new LabelCreateDTO();
            labelData.setName(name);
            var label = labelMapper.map(labelData);
            labelRepository.save(label);
        });
    }
}
