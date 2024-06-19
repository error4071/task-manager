package hexlet.code.controller.api;

import hexlet.code.dto.LoginDTO;
import hexlet.code.utils.JWTUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Operation(summary = "Authenticates the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authorization", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })

    @PostMapping("/login")
    public String create(@RequestBody LoginDTO loginDTO) {
        var authentication = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        authenticationManager.authenticate(authentication);

        var token = jwtUtils.generateToken(loginDTO.getUsername());
        return token;
    }
}
