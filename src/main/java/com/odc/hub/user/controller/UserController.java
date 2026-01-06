package com.odc.hub.user.controller;

import com.odc.hub.user.dto.ProfileResponseDto;
import com.odc.hub.user.dto.UpdateProfileRequest;
import com.odc.hub.user.dto.UserResponseDto;
import com.odc.hub.user.mapper.UserMapper;
import com.odc.hub.user.model.AccountStatus;
import com.odc.hub.user.model.Role;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
import com.odc.hub.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/profile")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public UserController(UserRepository userRepository,
                              PasswordEncoder passwordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @PatchMapping
    public ProfileResponseDto updateProfile(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return userService.updateProfile(request);
    }

    @GetMapping
    public ProfileResponseDto me() {
        return userService.getCurrentProfile();
    }

//    @GetMapping("/create-admin")
//    public User createAdmin() {
//
//        if (userRepository.existsByEmail("admin@odc.com")) {
//            throw new RuntimeException("Admin already exists");
//        }
//
//        User admin = new User();
//        admin.setEmail("admin@odc.com");
//        admin.setPassword(passwordEncoder.encode("Admin@123"));
//        admin.setRole(Role.ADMIN);
//        admin.setStatus(AccountStatus.ACTIVE);
//
//        return userRepository.save(admin);
//    }

//    @GetMapping("/create-user")
//    public User create() {
//        User user = new User(
//                "test@odc.com",
//                passwordEncoder.encode("Test123!"),
//                Role.BOOTCAMPER
//        );
//        return userRepository.save(user);
//    }

}
