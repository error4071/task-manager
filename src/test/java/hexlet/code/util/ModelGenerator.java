package hexlet.code.util;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;

import hexlet.code.model.Task;
import hexlet.code.model.User;
import lombok.Getter;
import net.datafaker.Faker;

@Getter
public class ModelGenerator {
    private Model<Task> taskModel;
    private Model<User> userModel;

    public ModelGenerator() {
        var faker = new Faker();
        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .toModel();

        taskModel = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .supply(Select.field(Task::getName), () -> faker.gameOfThrones().house())
                .supply(Select.field(Task::getDescription), () -> faker.gameOfThrones().quote())
                .toModel();
    }
}
