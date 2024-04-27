package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskFilterDTO {
    private String titleCont;
    private Integer assigneeId;
    private String status;
    private Integer labelId;
}
