package hexlet.code.dto.Label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class LabelUpdateDTO {

    @NotNull
    @Size(min = 3, max = 1000)
    private JsonNullable<String> name;
}
