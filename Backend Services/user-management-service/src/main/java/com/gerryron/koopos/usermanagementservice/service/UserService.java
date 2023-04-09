package com.gerryron.koopos.usermanagementservice.service;

import com.gerryron.koopos.usermanagementservice.entity.UserDetailEntity;
import com.gerryron.koopos.usermanagementservice.entity.UserEntity;
import com.gerryron.koopos.usermanagementservice.exception.KooposException;
import com.gerryron.koopos.usermanagementservice.repository.RoleRepository;
import com.gerryron.koopos.usermanagementservice.repository.UserDetailRepository;
import com.gerryron.koopos.usermanagementservice.repository.UserRepository;
import com.gerryron.koopos.usermanagementservice.shared.ApplicationCode;
import com.gerryron.koopos.usermanagementservice.shared.dto.ResponseStatus;
import com.gerryron.koopos.usermanagementservice.shared.request.SignUpRequest;
import com.gerryron.koopos.usermanagementservice.shared.response.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, UserDetailRepository userDetailRepository,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userDetailRepository = userDetailRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public RestResponse<Object> createUser(SignUpRequest request) {
        boolean isUsernameExists = userRepository.existsByUsername(request.getUsername());
        boolean isRoleExists = roleRepository.existsById(request.getRole());
        if (isUsernameExists) {
            throw new KooposException(ApplicationCode.USERNAME_ALREADY_USED);
        }
        if (!isRoleExists) {
            throw new KooposException(ApplicationCode.INVALID_ROLE);
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.getUsername());
        userEntity.setPassword(request.getPassword());
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
}
