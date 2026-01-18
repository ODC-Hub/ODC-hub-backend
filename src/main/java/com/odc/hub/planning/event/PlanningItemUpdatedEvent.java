package com.odc.hub.planning.event;

import com.odc.hub.planning.model.PlanningItem;
import lombok.Getter;

@Getter
public class PlanningItemUpdatedEvent {

    private final PlanningItem planningItem;

    public PlanningItemUpdatedEvent(PlanningItem planningItem) {
        this.planningItem = planningItem;
    }
}
