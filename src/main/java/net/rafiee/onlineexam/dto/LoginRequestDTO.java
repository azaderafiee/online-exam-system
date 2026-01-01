package net.rafiee.onlineexam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    
    @NotBlank(message = "نام کاربری الزامی است")
    private String username;
    
    @NotBlank(message = "رمز عبور الزامی است")
    private String password;
}