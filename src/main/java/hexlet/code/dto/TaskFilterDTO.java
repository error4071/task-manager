package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskFilterDTO {
    private String titleCont;
    private Integer id;
    private String slug;
    private Integer labelId;
}
