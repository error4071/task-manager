package hexlet.code.controller.api;

import hexlet.code.dto.User.UserCreateDTO;
import hexlet.code.dto.User.UserDTO;
import hexlet.code.dto.User.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import hexlet.code.utils.UserUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private UserRepository userRepository;
    private UserService userService;
    private UserMapper userMapper;
    private final UserUtils userUtils;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Show all users")
    ResponseEntity<List<UserDTO>> index() {
        var users = userRepository.findAll();
        var result =  users.stream()
                .map(x -> userMapper.map(x))
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(result);
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Show user by id")
    public UserDTO show(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new user")
    public UserDTO create(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        return userService.create(userCreateDTO);
    }

    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user data")
    @PreAuthorize(value = "@userService.findById(#id).getEmail() == authentication.name")
    public UserDTO update(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.update(id, userUpdateDTO);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user")
    @PreAuthorize(value = "@userService.findById(#id).getEmail() == authentication.name")
    public void delete(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
