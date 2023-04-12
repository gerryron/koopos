package com.gerryron.koopos.usermanagementservice.service;

import com.gerryron.koopos.usermanagementservice.config.JwtService;
import com.gerryron.koopos.usermanagementservice.entity.UserDetailEntity;
import com.gerryron.koopos.usermanagementservice.entity.UserEntity;
import com.gerryron.koopos.usermanagementservice.exception.KooposException;
import com.gerryron.koopos.usermanagementservice.repository.RoleRepository;
import com.gerryron.koopos.usermanagementservice.repository.UserDetailRepository;
import com.gerryron.koopos.usermanagementservice.repository.UserRepository;
import com.gerryron.koopos.usermanagementservice.shared.ApplicationCode;
import com.gerryron.koopos.usermanagementservice.shared.dto.ResponseStatus;
import com.gerryron.koopos.usermanagementservice.shared.request.SignInRequest;
import com.gerryron.koopos.usermanagementservice.shared.request.SignUpRequest;
import com.gerryron.koopos.usermanagementservice.shared.response.AuthenticationResponse;
import com.gerryron.koopos.usermanagementservice.shared.response.RestResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final RoleRepository roleRepository;


    public UserService(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
                       JwtService jwtService, UserRepository userRepository, UserDetailRepository userDetailRepository,
                       RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userDetailRepository = userDetailRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public RestResponse<Object> createUser(SignUpRequest request) {
        boolean isUsernameExists = userRepository
                .existsByUsernameOrUserDetail_Email(request.getUsername(), request.getEmail());
        boolean isRoleExists = roleRepository.existsById(request.getRole());
        if (isUsernameExists) {
            throw new KooposException(ApplicationCode.USERNAME_ALREADY_USED);
        }
        if (!isRoleExists) {
            throw new KooposException(ApplicationCode.INVALID_ROLE);
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.getUsername());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userEntity.setRoleId(request.getRole());

        UserDetailEntity userDetailEntity = new UserDetailEntity();
        userDetailEntity.setEmail(request.getEmail());
        userDetailEntity.setPhoneNumber(request.getPhoneNumber());
        userDetailEntity.setUser(userEntity);

        userEntity.setUserDetail(userDetailEntity);
        userRepository.save(userEntity);
        userDetailRepository.save(userDetailEntity);

        log.info("success create user with username: {}", request.getUsername());
        return RestResponse.builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .build();
    }

    public RestResponse<AuthenticationResponse> login(SignInRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        if (!authentication.isAuthenticated()) {
            log.info("username {} login with incorrect password", request.getUsername());
            throw new KooposException(ApplicationCode.LOGIN_FAILED);
        }

        String token = jwtService.generateToken(request.getUsername());

        return RestResponse.<AuthenticationResponse>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(AuthenticationResponse.builder()
                        .accessToken(token)
                        .build())
                .build();
    }

    public RestResponse<Object> validateToken(String token) {
        try {
            jwtService.validateToken(token);
        } catch (ExpiredJwtException expiredJwtException) {
            throw new KooposException(ApplicationCode.EXPIRED_AUTHENTICATION);
        }

        return RestResponse.builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .build();
    }
}
