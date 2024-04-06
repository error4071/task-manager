package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Task;

import lombok.Getter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingTarget;

@Getter
@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public abstract class TaskMapper {

    @Mapping(target = "index", source = "index")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "assignee", source = "assignee")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(target = "index", source = "index")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "assignee", source = "assignee")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus")
    public abstract TaskDTO map(Task model);

    @Mapping(target = "index", source = "index")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "assignee", source = "assignee")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);
}
