package hexlet.code.service;

import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.dto.Task.TaskDTO;
import hexlet.code.dto.Task.TaskUpdateDTO;

import hexlet.code.dto.TaskFilterDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskFilter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {

    private TaskMapper taskMapper;
    private TaskRepository taskRepository;
    private TaskFilter taskFilter;

    public List<TaskDTO> getAll(TaskFilterDTO filter) {
        var task = taskFilter.build(filter);
        return taskRepository.findAll().stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO findById(Long id) {
        var task = taskRepository.findById(id).orElseThrow();
        return taskMapper.map(task);
    }

    public TaskDTO create(TaskCreateDTO taskCreate) {
        var task = taskMapper.map(taskCreate);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO update(Long id, TaskUpdateDTO taskUpdateDTO) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        taskMapper.update(taskUpdateDTO, task);
        taskRepository.save(task);
        var taskDto = taskMapper.map(task);
        return taskDto;
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
