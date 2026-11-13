
package com.example.hospitalmanager.config;

import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {

            // Create default Admin account
            if (!userRepository.existsByEmail("admin@hospital.com")) {

                AppUser admin = new AppUser();

                admin.setUsername("admin");
                admin.setEmail("admin@hospital.com");
                admin.setPassword(
                        passwordEncoder.encode("Admin123")
                );
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);

                System.out.println("Default Admin account created!");
            }

            // Create default Doctor account
            if (!userRepository.existsByEmail("doctor@hospital.com")) {

                AppUser doctor = new AppUser();

                doctor.setUsername("doctor");
                doctor.setEmail("doctor@hospital.com");
                doctor.setPassword(
                        passwordEncoder.encode("Doctor123")
                );
                doctor.setRole(Role.DOCTOR);

                userRepository.save(doctor);

                System.out.println("Default Doctor account created!");
            }
        };
    }
}
