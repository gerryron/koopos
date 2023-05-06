package com.gerryron.kooposservice.service;

import com.gerryron.kooposservice.config.auth.JwtService;
import com.gerryron.kooposservice.dto.RestResponse;
import com.gerryron.kooposservice.dto.request.SignInRequest;
import com.gerryron.kooposservice.dto.request.SignUpRequest;
import com.gerryron.kooposservice.dto.response.SignInResponse;
import com.gerryron.kooposservice.entity.UserDetailEntity;
import com.gerryron.kooposservice.entity.UserEntity;
import com.gerryron.kooposservice.enums.ApplicationCode;
import com.gerryron.kooposservice.exception.AuthenticationException;
import com.gerryron.kooposservice.exception.ConflictException;
import com.gerryron.kooposservice.exception.NotFoundException;
import com.gerryron.kooposservice.helper.ErrorDetailHelper;
import com.gerryron.kooposservice.repository.RoleRepository;
import com.gerryron.kooposservice.repository.UserDetailRepository;
import com.gerryron.kooposservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public RestResponse<Object> createUser(SignUpRequest request) {
        if (userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            log.warn("User with username: {} or email: {} already exists",
                    request.getUsername(), request.getEmail());
            throw new ConflictException(ErrorDetailHelper.alreadyExistsSingletonList("username or email"));
        }
        if (!roleRepository.existsById(request.getRole())) {
            log.warn("User with username: {} has an invalid role", request.getUsername());
            throw new NotFoundException(ErrorDetailHelper.notFoundSingletonList("role"));
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.getUsername());
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userEntity.setRoleId(request.getRole());
        userEntity.setCreatedDate(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(userEntity);

        UserDetailEntity userDetailEntity = new UserDetailEntity();
        userDetailEntity.setUser(savedUser);
        userDetailEntity.setFirstName(request.getFirstName());
        userDetailEntity.setLastName(request.getLastName());
        userDetailEntity.setPhoneNumber(request.getPhoneNumber());
        userDetailEntity.setAddress(request.getAddress());
        userDetailEntity.setCreatedDate(LocalDateTime.now());
        userDetailRepository.save(userDetailEntity);

        log.info("User with username: {} created successfully", request.getUsername());
        return RestResponse.builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .build();
    }

    public RestResponse<SignInResponse> signIn(SignInRequest request) {
        UserEntity userEntity = userRepository
                .findByUsernameOrEmail(request.getUsername(), request.getEmail())
                .orElseThrow(() -> new NotFoundException(
                        ErrorDetailHelper.notFoundSingletonList("username or email")));

        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userEntity.getUsername(), request.getPassword()));
            if (!authentication.isAuthenticated()) {
                log.info("User with username: {} is not authenticated", request.getUsername());
                throw new AuthenticationException("is not authenticated");
            }
        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage());
        }

        log.info("User with username: {} login successfully", request.getUsername());
        return RestResponse.<SignInResponse>builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .data(SignInResponse.builder()
                        .accessToken(jwtService.generateToken(userEntity.getUsername()))
                        .build())
                .build();
    }
}
