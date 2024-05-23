package hexlet.code.dto.Task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class TaskUpdateDTO {

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;
    private JsonNullable<Set<Long>> taskLabelIds;

    @NotBlank
    private JsonNullable<String> title;
    private JsonNullable<String> content;
    private JsonNullable<Integer> index;

    @NotBlank
    private JsonNullable<String> status;
}
