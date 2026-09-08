package com.teacher.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teacher.common.constant.ErrorCode;
import com.teacher.common.exception.BadRequestException;
import com.teacher.common.service.BaseService;
import com.teacher.common.util.DTOMapper;
import com.teacher.dto.user.CreateUserRequest;
import com.teacher.dto.user.UpdateUserRequest;
import com.teacher.dto.user.UserDTO;
import com.teacher.dto.user.UserLoginRequest;
import com.teacher.dto.user.UserLoginResponse;
import com.teacher.entity.User;
import com.teacher.entity.User.UserStatus;
import com.teacher.repository.IUserRepository;
import com.teacher.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService extends BaseService<User, UUID> {

    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final DTOMapper dtoMapper;
    private final JwtService jwtService;

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAllById(Collection<UUID> ids) {
        return userRepository.findAllById(ids);
    }

    @Override
    public String notFoundByIdErrorCode() {
        return ErrorCode.ERROR_NOT_FOUND_USER_BY_ID;
    }

    @Override
    public String notFoundByIdsErrorCode() {
        return ErrorCode.ERROR_NOT_FOUND_SOME_USERS_BY_ID;
    }

    /**
     * Create a new user (Registration)
     */
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException(ErrorCode.ERROR_EMAIL_EXISTED);
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .status(request.getStatus() != null ? request.getStatus() : UserStatus.Active)
                .createdAt(now)
                .updateAt(now)
                .build();

        User saved = userRepository.save(user);
        return dtoMapper.map(saved, UserDTO.class);
    }

    /**
     * Update an existing user's details (Only allows phone, password, full_name)
     */
    @Transactional
    public UserDTO updateUser(UUID userId, UpdateUserRequest request) {
        User user = findByIdOrThrow(userId);

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        user.setUpdateAt(OffsetDateTime.now(ZoneOffset.UTC));
        User updated = userRepository.save(user);
        return dtoMapper.map(updated, UserDTO.class);
    }

    /**
     * Retrieve user details by ID
     */
    public UserDTO getUserById(UUID userId) {
        User user = findByIdOrThrow(userId);
        return dtoMapper.map(user, UserDTO.class);
    }

    /**
     * User Login / Sign-in
     */
    @Transactional
    public UserLoginResponse login(UserLoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorCode.ERROR_USERNAME_PASSWORD_INVALID));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException(ErrorCode.ERROR_USERNAME_PASSWORD_INVALID);
        }

        if (user.getStatus() != UserStatus.Active) {
            throw new BadRequestException(ErrorCode.ERROR_USER_IS_NOT_AVAILABLE);
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        user.setLastLoginAt(now);
        userRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        OffsetDateTime expiresAt = OffsetDateTime.ofInstant(
                Instant.now().plusMillis(jwtService.getExpirationMs()), ZoneOffset.UTC);

        return UserLoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresAt(expiresAt)
                .user(dtoMapper.map(user, UserDTO.class))
                .build();
    }

    /**
     * User Logout (stateless — client discards token)
     */
    public void logout(UUID userId) {
        User user = findByIdOrThrow(userId);
        if (user.getStatus() != UserStatus.Active) {
            throw new BadRequestException(ErrorCode.ERROR_USER_IS_NOT_AVAILABLE);
        }
    }
}
