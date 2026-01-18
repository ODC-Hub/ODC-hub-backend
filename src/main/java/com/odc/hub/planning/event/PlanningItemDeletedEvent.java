package com.odc.hub.planning.event;

import lombok.Getter;

@Getter
public class PlanningItemDeletedEvent {

    private final String planningItemId;

    public PlanningItemDeletedEvent(String planningItemId) {
        this.planningItemId = planningItemId;
    }
}
