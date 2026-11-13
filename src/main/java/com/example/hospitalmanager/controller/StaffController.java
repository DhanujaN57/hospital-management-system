
package com.example.hospitalmanager.controller;

import com.example.hospitalmanager.dto.UserResponse;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Get all staff members
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllStaff() {

        List<UserResponse> staff = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() != Role.PATIENT)
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole()
                ))
                .toList();

        return ResponseEntity.ok(staff);
    }

    // Create staff member
    @PostMapping
    public ResponseEntity<UserResponse> createStaff(
            @RequestBody StaffRequest request
    ) {

        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Username is already taken"
            );
        }

        if (request.role() == null || request.role() == Role.PATIENT) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid staff role"
            );
        }

        AppUser user = new AppUser();

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(
                passwordEncoder.encode(request.password())
        );
        user.setRole(request.role());

        AppUser savedUser = userRepository.save(user);

        UserResponse response = new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Request DTO for creating staff
    public record StaffRequest(
            String username,
            String email,
            String password,
            Role role
    ) {
    }
}
