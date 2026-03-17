package com.example.phishguard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.phishguard.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}