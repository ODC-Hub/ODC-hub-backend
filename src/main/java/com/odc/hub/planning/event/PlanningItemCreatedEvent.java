package com.odc.hub.planning.event;

import com.odc.hub.planning.model.PlanningItem;
import lombok.Getter;

@Getter
public class PlanningItemCreatedEvent {

    private final PlanningItem planningItem;

    public PlanningItemCreatedEvent(PlanningItem planningItem) {
        this.planningItem = planningItem;
    }
}
