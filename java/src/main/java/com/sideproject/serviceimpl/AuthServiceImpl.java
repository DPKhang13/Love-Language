package com.sideproject.serviceimpl;

import com.sideproject.dto.LoginRequest;
import com.sideproject.dto.LoginResponse;
import com.sideproject.dto.RegisterRequest;
import com.sideproject.dto.UserDto;
import com.sideproject.entity.User;
import com.sideproject.exception.BadRequestException;
import com.sideproject.exception.DuplicateResourceException;
import com.sideproject.exception.ResourceNotFoundException;
import com.sideproject.repository.UserRepository;
import com.sideproject.service.AuthService;
import com.sideproject.utils.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // Validate input
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email cannot be empty");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password cannot be empty");
        }

        // Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequest.getEmail()));

        // Check if user is active
        if (!user.getIsActive()) {
            throw new BadRequestException("User account is inactive");
        }

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        // Generate tokens
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        // Map user to DTO
        UserDto userDto = modelMapper.map(user, UserDto.class);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userDto)
                .build();
    }

    @Override
    public UserDto register(RegisterRequest registerRequest) {
        // Validate input
        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email cannot be empty");
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password cannot be empty");
        }
        if (registerRequest.getFullName() == null || registerRequest.getFullName().trim().isEmpty()) {
            throw new BadRequestException("Full name cannot be empty");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + registerRequest.getEmail());
        }

        // Create new user
        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .isActive(true)
                .isDeleted(false)
                .build();

        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        // Validate refresh token
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new BadRequestException("Refresh token cannot be empty");
        }

        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        // Extract email from refresh token
        String email = jwtUtil.extractSubjectFromRefreshToken(refreshToken);

        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Generate new tokens
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());

        String newAccessToken = jwtUtil.generateAccessToken(email, claims);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);

        UserDto userDto = modelMapper.map(user, UserDto.class);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(userDto)
                .build();
    }
}

