package com.example.phishguard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.phishguard.model.Credential;
import com.example.phishguard.service.CredentialService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/credentials")
public class CredentialController {

    @Autowired
    private CredentialService service;

    @PostMapping
    public Credential add(@RequestBody Credential credential) {
        return service.save(credential);
    }

    @GetMapping("/{userId}")
    public List<Credential> get(@PathVariable Long userId) {
        return service.getByUser(userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}