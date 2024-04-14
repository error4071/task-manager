package hexlet.code.dto.Label;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class LabelUpdateDTO {

    @NotBlank
    private JsonNullable<String> name;

    private JsonNullable<LocalDate> createdAt;
}
