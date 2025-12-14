package net.rafiee.onlineexam.controller;

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
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
    
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable UserRole role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getUsersByStatus(@PathVariable UserStatus status) {
        return ResponseEntity.ok(userService.getUsersByStatus(status));
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> searchUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchUsers(keyword));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        return ResponseEntity.ok(userService.updateUser(id, updateDTO));
    }
    
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> approveUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.approveUser(id));
    }
    
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> rejectUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.rejectUser(id));
    }
    
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> changeUserRole(
            @PathVariable Long id,
            @RequestParam UserRole role) {
        return ResponseEntity.ok(userService.changeUserRole(id, role));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}