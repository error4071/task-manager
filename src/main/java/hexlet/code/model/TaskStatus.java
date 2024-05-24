package hexlet.code.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@Table(name = "task_statuses")
@EqualsAndHashCode

public class TaskStatus implements BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @ToString.Include
    private Long id;

    @Column(unique = true)
    @NotBlank
    @ToString.Include
    private String name;

    @Column(unique = true)
    @NotBlank
    @ToString.Include
    private String slug;

    @CreatedDate
    private LocalDate createdAt;
}
