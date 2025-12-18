package com.odc.hub.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.odc.hub.user.model.User;

public interface UserRepository extends MongoRepository<User, String> {
}
