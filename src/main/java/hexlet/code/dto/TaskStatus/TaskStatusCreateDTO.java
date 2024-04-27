package hexlet.code.dto.TaskStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusCreateDTO {

    @NotNull
    private String name;

    @NotNull
    private String slug;
}
