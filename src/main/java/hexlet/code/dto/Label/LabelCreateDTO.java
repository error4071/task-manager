package hexlet.code.dto.Label;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LabelCreateDTO {
    @Column(unique = true)
    @NotBlank
    @Size(min = 3, max = 1000)
    private String name;

    private LocalDate createdAt;
}
