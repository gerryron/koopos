package com.gerryron.koopos.usermanagementservice.repository;

import com.gerryron.koopos.usermanagementservice.entity.UserDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailRepository extends JpaRepository<UserDetailEntity, Integer> {
}
