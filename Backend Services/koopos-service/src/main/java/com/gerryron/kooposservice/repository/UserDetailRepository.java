package com.gerryron.kooposservice.repository;

import com.gerryron.kooposservice.entity.UserDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailRepository extends JpaRepository<UserDetailEntity, Integer> {
}
