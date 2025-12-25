package net.rafiee.onlineexam.service;


import net.rafiee.onlineexam.dto.*;
import net.rafiee.onlineexam.enumuration.UserRole;
import net.rafiee.onlineexam.enumuration.UserStatus;

import java.util.List;

public interface UserService {

    UserResponseDTO registerUser(UserRegistrationDTO registrationDTO);

    UserResponseDTO getUserById(Long id);

    UserResponseDTO getUserByUsername(String username);

    List<UserResponseDTO> getAllUsers();

    List<UserResponseDTO> getUsersByRole(UserRole role);

    List<UserResponseDTO> getUsersByStatus(UserStatus status);

    List<UserResponseDTO> searchUsers(String keyword);

    // متد جدید: جستجوی پیشرفته با فیلتر
    List<UserResponseDTO> searchUsersWithFilter(String keyword, UserRole role, UserStatus status);

    UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO);

    UserResponseDTO approveUser(Long id);

    UserResponseDTO rejectUser(Long id);

    UserResponseDTO changeUserRole(Long id, UserRole newRole);

    void deleteUser(Long id);

    LoginResponseDTO login(LoginRequestDTO loginRequest);
}
