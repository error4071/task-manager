package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCreateDTO {

    private String firstName;

    private String lastName;

    @Email
    private String email;

    @Size(min = 3, max = 100)
    @NotNull
    private String password;
}
