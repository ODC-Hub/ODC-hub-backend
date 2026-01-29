package com.odc.hub.admin.controller;

import com.odc.hub.user.dto.UserResponseDto;
import com.odc.hub.user.model.AccountStatus;
import com.odc.hub.user.model.Role;
import com.odc.hub.user.model.User;
import com.odc.hub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/pending")
    public List<UserResponseDto> getPendingUsers() {
        return userService.getPendingUsers();
    }

    @GetMapping("/search")
    public List<UserResponseDto> search(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) AccountStatus status) {
        return userService.searchUsers(email, role, status);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable String id) {
        userService.approveUser(id);
        return ResponseEntity.ok("User approved, activation email sent");
    }

    @DeleteMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable String id) {
        userService.rejectUser(id);
        return ResponseEntity.ok("User rejected");
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<?> disableUser(@PathVariable String id) {
        userService.disableUser(id);
        return ResponseEntity.ok("User disabled");
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<?> changeRole(
            @PathVariable String id,
            @RequestParam Role role) {
        userService.changeUserRole(id, role);
        return ResponseEntity.ok("Role updated");
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable String id,
            @RequestParam AccountStatus status) {
        userService.changeStatus(id, status);
        return ResponseEntity.ok("Status updated");
    }

}
