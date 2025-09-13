package com.iseem_backend.application.init;

import com.iseem_backend.application.enums.Role;
import com.iseem_backend.application.model.User;
import com.iseem_backend.application.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.countByRole(Role.ADMINISTRATION) == 0) {
            User admin = User.builder()
                    .email("admin@example.com")
                    .prenom("Admin")
                    .nom("User")
                    .telephone("0000000000")
                    .role(Role.ADMINISTRATION)
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user created!");
        }
    }
}
