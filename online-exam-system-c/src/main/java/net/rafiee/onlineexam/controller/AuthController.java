package net.rafiee.onlineexam.controller;

import net.rafiee.onlineexam.dto.LoginRequestDTO;
import net.rafiee.onlineexam.dto.LoginResponseDTO;
import net.rafiee.onlineexam.dto.UserRegistrationDTO;
import net.rafiee.onlineexam.dto.UserResponseDTO;
import net.rafiee.onlineexam.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserResponseDTO user = userService.registerUser(registrationDTO);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}