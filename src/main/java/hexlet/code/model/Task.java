package hexlet.code.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@Table(name = "tasks")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task {
    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ToString.Include
    @NotBlank
    @Size(min = 1)
    private String name;

    @ToString.Include
    private Integer index;

    @ToString.Include
    private String description;

    @NotNull
    @ToString.Include
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private TaskStatus taskStatus;

    @ToString.Include
    @ManyToOne(fetch = FetchType.EAGER)
    private User assignee;

    @CreatedDate
    private LocalDate createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Include
    private Set<Label> labels;
}
