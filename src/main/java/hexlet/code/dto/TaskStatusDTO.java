package hexlet.code.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class TaskStatusDTO {
    private Long id;
    private String name;
    private String slug;
    private LocalDate createdAt;
}
