package hexlet.code.dto.Task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TaskCreateDTO {

    private Integer index;

    @JsonProperty("assignee_id")
    private Long assigneeId;
    private Set<Long> taskLabelIds;

    @NotBlank
    private String title;

    private String content;

    @NotBlank
    private String status;
}
