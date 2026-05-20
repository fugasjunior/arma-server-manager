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

@Component
@Slf4j
class SecurityBootstrap {

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
            @Value("${auth.username}") String bootstrapUsername,
            @Value("${auth.password}") String bootstrapPassword) {
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

        User admin = new User();
        admin.setUsername(bootstrapUsername);
        admin.setPasswordHash(passwordEncoder.encode(bootstrapPassword));
        admin.getRoles().add(adminRole);
        userRepository.save(admin);

        log.info("Seeded initial admin user '{}'", bootstrapUsername);
    }
}
