package net.rafiee.onlineexam.dto;

import net.rafiee.onlineexam.enumuration.UserRole;
import net.rafiee.onlineexam.enumuration.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;
    private UserStatus status;
    private String phoneNumber;
    private LocalDateTime createdAt;
}