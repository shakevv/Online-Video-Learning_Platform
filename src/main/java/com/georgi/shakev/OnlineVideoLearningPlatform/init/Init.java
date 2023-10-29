package com.georgi.shakev.OnlineVideoLearningPlatform.init;

import com.georgi.shakev.OnlineVideoLearningPlatform.entity.User;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Init implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) {
        if(userRepository.getByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Adminpass1!"));
            admin.getRoles().add("ROLE_ADMIN");
            userRepository.save(admin);
        }
    }
}
