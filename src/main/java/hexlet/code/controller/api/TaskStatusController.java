package hexlet.code.controller.api;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class TaskStatusController {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Autowired
    private TaskStatusService taskStatusService;

    @GetMapping("/task_statuses")
    public List<TaskStatusDTO> index() {
        var taskStatuses = taskStatusRepository.findAll();
        return taskStatuses.stream()
                .map(x -> taskStatusMapper.map(x))
                .toList();
    }

    @GetMapping("/task_statuses/{id}")
    public TaskStatusDTO show(@PathVariable Long id) {
        var taskStatuses = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + " not found"));
        var taskStatusDto = taskStatusMapper.map(taskStatuses);
        return taskStatusDto;
    }

    @PostMapping("/task_statuses")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDTO create(@RequestBody @Valid TaskStatusCreateDTO taskStatusCreateDTO) {
        var taskStatuses = taskStatusMapper.map(taskStatusCreateDTO);
        taskStatusRepository.save(taskStatuses);
        var taskStatusDto = taskStatusMapper.map(taskStatuses);
        return taskStatusDto;
    }

    @PutMapping("/task_statuses/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO update(@PathVariable Long id, @RequestBody TaskStatusUpdateDTO taskStatusUpdateDTO) {
        return taskStatusService.update(id, taskStatusUpdateDTO);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskStatusRepository.deleteById(id);
    }
}
