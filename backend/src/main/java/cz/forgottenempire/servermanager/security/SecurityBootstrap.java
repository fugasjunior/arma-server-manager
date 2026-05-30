package cz.forgottenempire.servermanager.security;

import cz.forgottenempire.servermanager.security.role.Role;
import cz.forgottenempire.servermanager.security.role.RoleRepository;
import cz.forgottenempire.servermanager.security.user.User;
import cz.forgottenempire.servermanager.security.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@Slf4j
class SecurityBootstrap {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final String bootstrapUsername;
    private final String bootstrapPassword;

    @Autowired
    SecurityBootstrap(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            @Value("${auth.username:admin}") String bootstrapUsername,
            @Value("${auth.password:#{null}}") String bootstrapPassword) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.bootstrapUsername = bootstrapUsername;
        this.bootstrapPassword = bootstrapPassword;
    }

    @PostConstruct
    void seedAdminUser() {
        if (userRepository.count() > 0) {
            return;
        }

        Role adminRole = roleRepository.findByName("Admin")
                .orElseThrow(() -> new IllegalStateException("Admin role not found — migration V1_3_6 may not have run"));

        boolean passwordProvided = bootstrapPassword != null && !bootstrapPassword.isBlank();
        String password = passwordProvided ? bootstrapPassword : generatePassword();
        String username = (bootstrapUsername != null && !bootstrapUsername.isBlank()) ? bootstrapUsername : "admin";

        User admin = new User();
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.getRoles().add(adminRole);
        userRepository.save(admin);

        if (!passwordProvided) {
            log.info("=================================================================");
            log.info("  Initial admin credentials (shown only once):");
            log.info("  Username: {}", username);
            log.info("  Password: {}", password);
            log.info("=================================================================");
        } else {
            log.info("Seeded initial admin user '{}'", username);
        }
    }

    private String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
