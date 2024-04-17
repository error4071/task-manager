package hexlet.code.dto.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
@AllArgsConstructor
public class TaskStatusUpdateDTO {

    @NotBlank
    private JsonNullable<String> name;

    @NotBlank
    private JsonNullable<String> slug;
}
