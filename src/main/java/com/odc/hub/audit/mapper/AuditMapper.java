package com.odc.hub.audit.mapper;

import com.odc.hub.audit.dto.AuditDTO;
import com.odc.hub.audit.model.AuditLog;

public class AuditMapper {

    public static AuditDTO toDto(AuditLog log) {
        return new AuditDTO(
                log.getActorEmail(),
                log.getTargetUserId(),
                log.getAction(),
                log.getOldValue(),
                log.getNewValue(),
                log.getCreatedAt()
        );
    }
}
