package com.odc.hub.filrouge.dto;

import com.odc.hub.filrouge.enums.WorkItemStatus;

public record UpdateWorkItemStatusRequest(
        WorkItemStatus status
) {}
