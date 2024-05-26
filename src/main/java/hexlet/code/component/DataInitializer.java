package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

import java.util.List;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final CustomUserDetailsService userService;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;

    @Override
    public void run(ApplicationArguments args) {
        User admin = new User();
        admin.setEmail("hexlet@example.com");
        admin.setPasswordDigest("qwerty");
        var statuses = createTaskStatuses();
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            userService.createUser(admin);
        }
        for (var status: statuses) {
            taskStatusRepository.save(status);
        }
        var label1 = new Label();
        label1.setName("bug");
        labelRepository.save(label1);
        var label2 = new Label();
        label2.setName("feature");
        labelRepository.save(label2);
    }

    private static List<TaskStatus> createTaskStatuses() {
        TaskStatus draft = new TaskStatus();
        draft.setName("Draft");
        draft.setSlug("draft");
        TaskStatus toReview = new TaskStatus();
        toReview.setName("To Review");
        toReview.setSlug("to_review");
        TaskStatus toBeFixed = new TaskStatus();
        toBeFixed.setName("To Be Fixed");
        toBeFixed.setSlug("to_be_fixed");
        TaskStatus toPublish = new TaskStatus();
        toPublish.setName("To Publish");
        toPublish.setSlug("to_publish");
        TaskStatus published = new TaskStatus();
        published.setName("Published");
        published.setSlug("published");
        return List.of(draft, toReview, toBeFixed, toPublish, published);
    }
}
