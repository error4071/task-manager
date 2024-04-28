package hexlet.code.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
    @GetMapping(path = "/welcome")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Welcome page")
    public String welcome() {
        return "Welcome to Spring";
    }
}
