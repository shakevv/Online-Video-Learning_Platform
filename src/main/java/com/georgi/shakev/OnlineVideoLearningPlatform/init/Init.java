package com.georgi.shakev.OnlineVideoLearningPlatform.init;

import com.georgi.shakev.OnlineVideoLearningPlatform.entity.User;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Init implements CommandLineRunner {
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public Init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if(userRepository.getByUsername(DEFAULT_ADMIN_USERNAME).isEmpty()) {
            User admin = new User();
            admin.setUsername(DEFAULT_ADMIN_USERNAME);
            admin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
            admin.getRoles().add(ROLE_ADMIN);
            userRepository.save(admin);
        }
    }
}
