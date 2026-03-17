package com.example.phishguard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.phishguard.model.Credential;
import java.util.List;

public interface CredentialRepository extends JpaRepository<Credential, Long> {
    List<Credential> findByUserId(Long userId);
}