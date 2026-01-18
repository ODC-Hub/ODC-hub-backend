package com.odc.hub.planning.mapper;

import com.odc.hub.planning.dto.PlanningItemRequest;
import com.odc.hub.planning.dto.PlanningItemResponse;
import com.odc.hub.planning.model.PlanningItem;
import org.springframework.stereotype.Component;

@Component
public class PlanningItemMapper {

    public PlanningItem toEntity(PlanningItemRequest dto, String creatorId) {
        return PlanningItem.builder()
                .title(dto.title())
                .description(dto.description())
                .type(dto.type())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .userIds(dto.userIds())
                .tags(dto.tags())
                .createdBy(creatorId)
                .build();
    }

    public PlanningItemResponse toDto(PlanningItem item) {
        return new PlanningItemResponse(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getType(),
                item.getStartDate(),
                item.getEndDate(),
                item.getUserIds(),
                item.getTags(),
                item.getCreatedBy()
        );
    }
}
