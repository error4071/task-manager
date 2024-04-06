package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;

import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskStatusService {

    private TaskStatusMapper taskStatusMapper;

    private TaskStatusRepository taskStatusRepository;

    public List<TaskStatusDTO> getAll() {
        var taskStatuses = taskStatusRepository.findAll();
        return taskStatuses.stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO findById(Long id) {
        var taskStatus = taskStatusRepository.findById(id).orElseThrow();
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusCreate) {
        var taskStatuses = taskStatusMapper.map(taskStatusCreate);
        taskStatusRepository.save(taskStatuses);
        return taskStatusMapper.map(taskStatuses);
    }

    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO taskStatusUpdateDTO) {
        var taskStatuses = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + id + " not found"));
        taskStatusMapper.update(taskStatusUpdateDTO, taskStatuses);
        taskStatusRepository.save(taskStatuses);
        var taskStatusDto = taskStatusMapper.map(taskStatuses);
        return taskStatusDto;
    }

    public void delete(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
