package net.rafiee.onlineexam.controller;

import lombok.extern.slf4j.Slf4j;
import net.rafiee.onlineexam.dto.UserResponseDTO;
import net.rafiee.onlineexam.dto.UserUpdateDTO;
import net.rafiee.onlineexam.enumuration.UserRole;
import net.rafiee.onlineexam.enumuration.UserStatus;
import net.rafiee.onlineexam.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class UserController {
    
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("GET /api/users - Fetching all users");
        List<UserResponseDTO> users = userService.getAllUsers();
        log.info("Returned {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{} - Fetching user by ID", id);
        UserResponseDTO user = userService.getUserById(id);
        log.info("User found: {}", user.getUsername());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        log.info("GET /api/users/username/{} - Fetching user by username", username);
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable UserRole role) {
        log.info("GET /api/users/role/{} - Fetching users by role", role);
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getUsersByStatus(@PathVariable UserStatus status) {
        log.info("GET /api/users/status/{} - Fetching users by status", status);
        return ResponseEntity.ok(userService.getUsersByStatus(status));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> searchUsers(@RequestParam String keyword) {
        log.info("GET /api/users/search?keyword={} - Searching users", keyword);
        return ResponseEntity.ok(userService.searchUsers(keyword));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        log.info("PUT /api/users/{} - Updating user", id);
        log.debug("Update DTO: {}", updateDTO);
        UserResponseDTO updatedUser = userService.updateUser(id, updateDTO);
        log.info("User updated successfully: {}", updatedUser.getUsername());
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> approveUser(@PathVariable Long id) {
        log.info("PUT /api/users/{}/approve - Approving user", id);
        UserResponseDTO approvedUser = userService.approveUser(id);
        log.info("User approved: {}", approvedUser.getUsername());
        return ResponseEntity.ok(approvedUser);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> rejectUser(@PathVariable Long id) {
        log.info("PUT /api/users/{}/reject - Rejecting user", id);
        UserResponseDTO rejectedUser = userService.rejectUser(id);
        log.info("User rejected: {}", rejectedUser.getUsername());
        return ResponseEntity.ok(rejectedUser);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> changeUserRole(
            @PathVariable Long id,
            @RequestParam UserRole role) {
        log.info("PUT /api/users/{}/role?role={} - Changing user role", id, role);
        UserResponseDTO updatedUser = userService.changeUserRole(id, role);
        log.info("User role changed successfully");
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{} - Deleting user", id);
        userService.deleteUser(id);
        log.info("User deleted successfully");
        return ResponseEntity.noContent().build();
    }
}