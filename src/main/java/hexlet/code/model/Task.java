package hexlet.code.model;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @ToString.Include
    private String name;

    @ToString.Include
    private int index;

    @ToString.Include
    private String description;

    @ToString.Include
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private TaskStatus taskStatus;

    @ToString.Include
    @ManyToOne(fetch = FetchType.LAZY)
    private User assignee;

    @ManyToMany
    private Set<Label> labels = new HashSet<>();

    @CreatedDate
    private LocalDate createdAt;
}