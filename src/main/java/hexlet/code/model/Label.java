package hexlet.code.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "labels")
@Getter
@Setter
public class Label implements BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 1000)
    private String name;

    @CreatedDate
    private LocalDate createdAt;
}
