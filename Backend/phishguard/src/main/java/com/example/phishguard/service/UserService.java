package com.example.phishguard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.phishguard.model.User;
import com.example.phishguard.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    public User register(User user) {
        return userRepo.save(user);
    }

    public User login(String username, String password) {
        User user = userRepo.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}