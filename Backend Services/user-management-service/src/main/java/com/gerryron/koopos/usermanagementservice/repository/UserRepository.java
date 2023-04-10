package com.gerryron.koopos.usermanagementservice.repository;

import com.gerryron.koopos.usermanagementservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    boolean existsByUsernameOrUserDetail_Email(String username, String email);

    Optional<UserEntity> findByUsername(String username);

}
