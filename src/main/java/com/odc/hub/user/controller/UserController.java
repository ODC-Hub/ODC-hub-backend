package com.odc.hub.user.controller;

import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate; // 👈 ADD THIS

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 🔍 DEBUG ENDPOINT
    @GetMapping("/debug-db")
    public String debugDb() {
        return mongoTemplate.getDb().getName();
    }
    @GetMapping("/debug-users")
    public List<User> debugUsers() {
        return mongoTemplate.findAll(User.class, "users");
    }

}
