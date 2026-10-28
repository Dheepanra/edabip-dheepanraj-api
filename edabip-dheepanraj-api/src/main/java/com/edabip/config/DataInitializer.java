package com.edabip.config;

import com.edabip.entity.User;
import com.edabip.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * data.sql seeds users with the placeholder hash CHANGE_ME. On startup this replaces it with a BCrypt hash
 * of app.seed.password, so no password or hash is ever committed.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String PLACEHOLDER = "CHANGE_ME";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String seedPassword;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           @Value("${app.seed.password}") String seedPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedPassword = seedPassword;
    }

    @Override
    public void run(String... args) {
        List<User> pending = userRepository.findByPasswordHash(PLACEHOLDER);
        if (pending.isEmpty()) {
            return;
        }
        String hash = passwordEncoder.encode(seedPassword);
        pending.forEach(u -> u.setPasswordHash(hash));
        userRepository.saveAll(pending);
        log.info("Set BCrypt password hash for {} seeded user(s)", pending.size());
    }
}
