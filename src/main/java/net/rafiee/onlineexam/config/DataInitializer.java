package net.rafiee.onlineexam.config;

import net.rafiee.onlineexam.entity.User;
import net.rafiee.onlineexam.enumuration.UserRole;
import net.rafiee.onlineexam.enumuration.UserStatus;
import net.rafiee.onlineexam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("Initializing default users...");
                
                User admin = User.builder()
                        .username("admin")
                        .email("admin@exam.com")
                        .password(passwordEncoder.encode("admin123"))
                        .fullName("مدیر سیستم")
                        .role(UserRole.ADMIN)
                        .status(UserStatus.APPROVED)
                        .phoneNumber("09121234567")
                        .build();
                userRepository.save(admin);
                log.info("Admin user created: username=admin, password=admin123");
                
                User instructor = User.builder()
                        .username("instructor1")
                        .email("instructor1@exam.com")
                        .password(passwordEncoder.encode("instructor123"))
                        .fullName("دکتر احمدی")
                        .role(UserRole.INSTRUCTOR)
                        .status(UserStatus.APPROVED)
                        .phoneNumber("09121234568")
                        .build();
                userRepository.save(instructor);
                log.info("Instructor user created: username=instructor1, password=instructor123");
                
                User student = User.builder()
                        .username("student1")
                        .email("student1@exam.com")
                        .password(passwordEncoder.encode("student123"))
                        .fullName("علی محمدی")
                        .role(UserRole.STUDENT)
                        .status(UserStatus.APPROVED)
                        .phoneNumber("09121234569")
                        .build();
                userRepository.save(student);
                log.info("Student user created: username=student1, password=student123");
                
                log.info("Data initialization completed!");
            }
        };
    }
}