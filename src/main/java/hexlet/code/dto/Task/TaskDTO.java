package hexlet.code.dto.Task;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private int index;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date createdAt;

    private Long assigneeId;
    private String title;
    private String content;
    private String status;
    private Set<Long> taskLabelIds = new HashSet<>();
}
