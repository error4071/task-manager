package hexlet.code.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class WelcomeController {
    @GetMapping(path = "/welcome")
    @ResponseStatus(HttpStatus.OK)
    public String welcome() {
        return "Welcome to Spring";
    }
}
