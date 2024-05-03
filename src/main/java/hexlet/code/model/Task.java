package hexlet.code.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "tasks")
@Getter
@Setter

public class Task implements BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Integer index;

    @NotBlank
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @NotNull
    private TaskStatus taskStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    private User assignee;

    @CreatedDate
    private LocalDate createdAt;

}
