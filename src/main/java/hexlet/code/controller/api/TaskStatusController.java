package hexlet.code.controller.api;

import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;

import hexlet.code.utils.UserUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

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

    @Autowired
    private UserUtils userUtils;

    @GetMapping("/task_statuses")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<TaskStatusDTO>> index() {
        var taskStatuses = taskStatusRepository.findAll();
        var result =  taskStatuses.stream()
                .map(x -> taskStatusMapper.map(x))
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatuses.size()))
                .body(result);
    }

    @GetMapping("/task_statuses/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO show(@PathVariable Long id) {
        var taskStatuses = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + " not found"));
        var taskStatusDto = taskStatusMapper.map(taskStatuses);
        return taskStatusDto;
    }

    @PostMapping("/task_statuses")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@userUtils.isUser(#id)")
    public TaskStatusDTO create(@RequestBody @Valid TaskStatusCreateDTO taskStatusCreateDTO) {
        var taskStatuses = taskStatusMapper.map(taskStatusCreateDTO);
        taskStatusRepository.save(taskStatuses);
        var taskStatusDto = taskStatusMapper.map(taskStatuses);
        return taskStatusDto;
    }

    @PutMapping("/task_statuses/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@userUtils.isUser(#id)")
    public TaskStatusDTO update(@PathVariable Long id, @RequestBody TaskStatusUpdateDTO taskStatusUpdateDTO) {
        return taskStatusService.update(id, taskStatusUpdateDTO);
    }

    @DeleteMapping("/task_statuses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@userUtils.isUser(#id)")
    public void delete(@PathVariable Long id) {
        taskStatusRepository.deleteById(id);
    }
}
