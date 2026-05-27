package com.astitva.collabflow.user.repository;

import com.astitva.collabflow.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
