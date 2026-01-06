package com.odc.hub.user.repository;

import com.odc.hub.user.model.AccountStatus;
import com.odc.hub.user.model.Role;
import com.odc.hub.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    List<User> findAll();

    Optional<User> findByEmail(String email);

    List<User> findByEmailContainingIgnoreCase(String email);

    List<User> findByEmailContainingIgnoreCaseAndRole(String email, Role role);

    List<User> findByEmailContainingIgnoreCaseAndStatus(String email, AccountStatus status);

    List<User> findByEmailContainingIgnoreCaseAndRoleAndStatus(
            String email,
            Role role,
            AccountStatus status
    );

    List<User> findByRole(Role role);

    List<User> findByRoleAndStatus(Role role, AccountStatus status);

    Optional<User> findByActivationToken(String token);

    Optional<User> findByResetPasswordToken(String token);

    boolean existsByEmail(String email);

    List<User> findByStatus(AccountStatus status);

    long countByRole(Role role);


}
