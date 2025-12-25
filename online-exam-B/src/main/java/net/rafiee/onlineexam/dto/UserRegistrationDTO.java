package net.rafiee.onlineexam.dto;

import net.rafiee.onlineexam.enumuration.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    
    @NotBlank(message = "نام کاربری الزامی است")
    @Size(min = 3, max = 50, message = "نام کاربری باید بین 3 تا 50 کاراکتر باشد")
    private String username;
    
    @NotBlank(message = "ایمیل الزامی است")
    @Email(message = "فرمت ایمیل نامعتبر است")
    private String email;
    
    @NotBlank(message = "رمز عبور الزامی است")
    @Size(min = 6, message = "رمز عبور باید حداقل 6 کاراکتر باشد")
    private String password;
    
    @NotBlank(message = "نام کامل الزامی است")
    private String fullName;
    
    @NotNull(message = "نقش کاربر الزامی است")
    private UserRole role;
    
    private String phoneNumber;
}