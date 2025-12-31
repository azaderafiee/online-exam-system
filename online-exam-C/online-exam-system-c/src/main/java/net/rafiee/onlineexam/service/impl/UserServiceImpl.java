package net.rafiee.onlineexam.service.impl;

import net.rafiee.onlineexam.dto.*;
import net.rafiee.onlineexam.entity.User;
import net.rafiee.onlineexam.enumuration.UserRole;
import net.rafiee.onlineexam.enumuration.UserStatus;
import net.rafiee.onlineexam.exception.BadRequestException;
import net.rafiee.onlineexam.exception.DuplicateResourceException;
import net.rafiee.onlineexam.exception.ResourceNotFoundException;
import net.rafiee.onlineexam.exception.UnauthorizedException;
import net.rafiee.onlineexam.repository.UserRepository;
import net.rafiee.onlineexam.security.JwtTokenProvider;
import net.rafiee.onlineexam.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rafiee.onlineexam.specification.UserSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        log.info("Registering new user: {}", registrationDTO.getUsername());
        
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new DuplicateResourceException("نام کاربری قبلاً ثبت شده است");
        }
        
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new DuplicateResourceException("ایمیل قبلاً ثبت شده است");
        }
        
        User user = User.builder()
                .username(registrationDTO.getUsername())
                .email(registrationDTO.getEmail())
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .fullName(registrationDTO.getFullName())
                .role(registrationDTO.getRole())
                .status(UserStatus.PENDING)
                .phoneNumber(registrationDTO.getPhoneNumber())
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        return mapToUserResponseDTO(savedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("کاربر با شناسه " + id + " یافت نشد"));
        return mapToUserResponseDTO(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("کاربر با نام کاربری " + username + " یافت نشد"));
        return mapToUserResponseDTO(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByStatus(UserStatus status) {
        return userRepository.findByStatus(status).stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword).stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> searchUsersWithFilter(String keyword, UserRole role, UserStatus status) {
        log.info("Searching users with filters - keyword: {}, role: {}, status: {}", keyword, role, status);

        Specification<User> spec = UserSpecification.searchUsers(keyword, role, status);
        List<User> users = userRepository.findAll(spec);

        log.info("Found {} users matching criteria", users.size());
        return users.stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        log.info("Updating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("کاربر یافت نشد"));
        
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new DuplicateResourceException("ایمیل قبلاً توسط کاربر دیگری استفاده شده است");
            }
            user.setEmail(updateDTO.getEmail());
        }
        
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        
        if (updateDTO.getFullName() != null) {
            user.setFullName(updateDTO.getFullName());
        }
        
        if (updateDTO.getRole() != null) {
            user.setRole(updateDTO.getRole());
        }
        
        if (updateDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(updateDTO.getPhoneNumber());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getUsername());
        
        return mapToUserResponseDTO(updatedUser);
    }
    
    @Override
    public UserResponseDTO approveUser(Long id) {
        log.info("Approving user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("کاربر یافت نشد"));
        
        if (user.getStatus() == UserStatus.APPROVED) {
            throw new BadRequestException("کاربر قبلاً تأیید شده است");
        }
        
        user.setStatus(UserStatus.APPROVED);
        User approvedUser = userRepository.save(user);
        
        log.info("User approved successfully: {}", approvedUser.getUsername());
        return mapToUserResponseDTO(approvedUser);
    }
    
    @Override
    public UserResponseDTO rejectUser(Long id) {
        log.info("Rejecting user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("کاربر یافت نشد"));
        
        user.setStatus(UserStatus.REJECTED);
        User rejectedUser = userRepository.save(user);
        
        log.info("User rejected: {}", rejectedUser.getUsername());
        return mapToUserResponseDTO(rejectedUser);
    }
    
    @Override
    public UserResponseDTO changeUserRole(Long id, UserRole newRole) {
        log.info("Changing role for user with ID: {} to {}", id, newRole);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("کاربر یافت نشد"));
        
        user.setRole(newRole);
        User updatedUser = userRepository.save(user);
        
        log.info("User role changed successfully");
        return mapToUserResponseDTO(updatedUser);
    }
    
    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("کاربر یافت نشد"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName().equals(user.getUsername())) {
            throw new BadRequestException("شما نمی‌توانید حساب کاربری خود را حذف کنید");
        }

        if (user.getRole() == UserRole.ADMIN && user.getUsername().equals("admin")) {
            throw new BadRequestException("حذف مدیر اصلی سیستم مجاز نیست");
        }

        userRepository.delete(user);
        log.info("User deleted successfully");
    }
    
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UnauthorizedException("نام کاربری یا رمز عبور نادرست است"));
        
        if (user.getStatus() != UserStatus.APPROVED) {
            throw new UnauthorizedException("حساب کاربری شما هنوز تأیید نشده است");
        }
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        
        log.info("User logged in successfully: {}", loginRequest.getUsername());
        
        return LoginResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .user(mapToUserResponseDTO(user))
                .build();
    }
    
    private UserResponseDTO mapToUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
