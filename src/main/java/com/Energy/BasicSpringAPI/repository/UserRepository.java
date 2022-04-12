package com.Energy.BasicSpringAPI.repository;


import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.enumerators.Roles;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for the users table
 *
 * @class UserRepository
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAllByRole(Roles role);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @NotNull
    Optional<UserEntity> findById(@NotNull UUID uuid);
}
