package com.odc.hub.audit.repository;

import com.odc.hub.audit.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    Page<AuditLog> findAll(Pageable pageable);

    Page<AuditLog> findByActorEmail(String actorEmail, Pageable pageable);

    Page<AuditLog> findByAction(String action, Pageable pageable);

    Page<AuditLog> findByCreatedAtBetween(
            Instant from,
            Instant to,
            Pageable pageable
    );


}
