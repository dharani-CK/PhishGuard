package com.example.phishguard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.phishguard.model.Credential;
import com.example.phishguard.repository.CredentialRepository;

import java.util.List;

@Service
public class CredentialService {

    @Autowired
    private CredentialRepository repo;

    public Credential save(Credential credential) {
        return repo.save(credential);
    }

    public List<Credential> getByUser(Long userId) {
        return repo.findByUserId(userId);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}