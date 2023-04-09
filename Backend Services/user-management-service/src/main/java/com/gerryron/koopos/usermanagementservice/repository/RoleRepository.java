package com.gerryron.koopos.usermanagementservice.repository;

import com.gerryron.koopos.usermanagementservice.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
}
