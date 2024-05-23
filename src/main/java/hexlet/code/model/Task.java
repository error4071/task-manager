package hexlet.code.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Task implements BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ToString.Include
    private Integer index;

    @NotNull
    @ToString.Include
    private String name;

    @Column(columnDefinition = "TEXT")
    @ToString.Include
    private String description;

    @ManyToOne
    @NotNull
    @ToString.Include
    private TaskStatus taskStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Include
    private User assignee;

    @CreatedDate
    @ToString.Include
    private LocalDate createdAt;
}
