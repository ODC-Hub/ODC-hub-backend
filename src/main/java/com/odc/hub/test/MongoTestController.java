package com.odc.hub.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;

@RestController
@RequestMapping("/api/test")
public class MongoTestController {

    private final UserRepository userRepository;

    public MongoTestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/mongo")
    public String testMongo() {
        userRepository.save(new User("admin@odc.com", "ADMIN"));
        return "MongoDB connected — user saved";
    }
}
