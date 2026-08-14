package com.sayel.E_Commerce.config;

import com.sayel.E_Commerce.entity.User;
import com.sayel.E_Commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ensures a default admin account exists so the admin dashboard is usable
 * right after a fresh setup, without needing direct database access.
 * Override the credentials via ADMIN_EMAIL / ADMIN_PASSWORD env vars.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email:admin@shopsphere.com}")
    private String adminEmail;

    @Value("${admin.password:Admin@123}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setName("Store Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("======================================================");
            System.out.println(" Default admin account created:");
            System.out.println(" Email:    " + adminEmail);
            System.out.println(" Password: " + adminPassword);
            System.out.println(" Please change this password after first login.");
            System.out.println("======================================================");
        }
    }
}
