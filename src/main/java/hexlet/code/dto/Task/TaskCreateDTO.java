package hexlet.code.dto.Task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class TaskCreateDTO {

    private Long id;
    private int index;
    private LocalDate createdAt;
    private Long assigneeId;
    private String title;
    private String content;
    private String status;
    private Set<Long> taskLabelIds;
}
