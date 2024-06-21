package hexlet.code.utils;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserUtils {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow();
    }

    public boolean isAuthor(long id) {
        var userEmail = userRepository.findById(id).orElseThrow().getEmail();
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return userEmail.equals(authentication.getName());
    }
}
