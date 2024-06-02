package hexlet.code.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Task implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer index;

    @NotBlank
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "task_statuses")
    private TaskStatus taskStatus;

    @ManyToOne
    private User assignee;

    @ManyToMany
    private Set<Label> labels = new HashSet<>();

    @CreatedDate
    private LocalDate createdAt;
}
