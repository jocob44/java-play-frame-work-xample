package services;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import models.User;
import repositories.UserRepository;
import security.JwtService;
import security.PasswordService;

@Singleton
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    @Inject
    public AuthService(UserRepository userRepository, PasswordService passwordService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    public AuthResult register(String email, String rawPassword) {
        validateCredentials(email, rawPassword);
        String hash = passwordService.hash(rawPassword);
        User user = userRepository.create(email.trim().toLowerCase(), hash);
        String token = jwtService.createToken(user.getId(), user.getEmail());
        return new AuthResult(user.getId(), user.getEmail(), token);
    }

    public AuthResult login(String email, String rawPassword) {
        validateCredentials(email, rawPassword);
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!passwordService.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        String token = jwtService.createToken(user.getId(), user.getEmail());
        return new AuthResult(user.getId(), user.getEmail(), token);
    }

    private void validateCredentials(String email, String password) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
    }

    public record AuthResult(long userId, String email, String token) {
    }
}
