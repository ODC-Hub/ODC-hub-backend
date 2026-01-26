package com.odc.hub.audit.service;

import com.odc.hub.audit.model.AuditLog;
import com.odc.hub.audit.repository.AuditLogRepository;
import com.odc.hub.user.model.User;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(
            User actor,
            User target,
            String action,
            String oldValue,
            String newValue
    ) {
        AuditLog log = new AuditLog();
        log.setActorId(actor.getId());
        log.setActorEmail(actor.getEmail());
        log.setTargetUserId(target.getId());
        log.setAction(action);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setCreatedAt(Instant.now());

        auditLogRepository.save(log);
    }

}
