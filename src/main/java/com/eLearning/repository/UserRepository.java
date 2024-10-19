package com.eLearning.repository;

import com.eLearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByEmail(String email);
}
