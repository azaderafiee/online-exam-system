package net.rafiee.onlineexam.dto;

import net.rafiee.onlineexam.enumuration.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    
    @Email(message = "فرمت ایمیل نامعتبر است")
    private String email;
    
    @Size(min = 6, message = "رمز عبور باید حداقل 6 کاراکتر باشد")
    private String password;
    
    private String fullName;
    
    private UserRole role;
    
    private String phoneNumber;
}