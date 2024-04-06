package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class TaskDTO {

    private Long id;
    private String name;
    private Integer index;
    private String description;
    private String taskStatus;
    private String assignee;
    private LocalDate createdAt;
}
