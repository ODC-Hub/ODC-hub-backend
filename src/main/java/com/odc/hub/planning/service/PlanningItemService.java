package com.odc.hub.planning.service;

import com.odc.hub.planning.dto.PlanningItemRequest;
import com.odc.hub.planning.dto.PlanningItemResponse;
import com.odc.hub.planning.mapper.PlanningItemMapper;
import com.odc.hub.planning.model.PlanningItem;
import com.odc.hub.planning.repository.PlanningItemRepository;
import com.odc.hub.user.model.Role;
import com.odc.hub.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlanningItemService {

    private final PlanningItemRepository repository;
    private final PlanningItemMapper mapper;

    public PlanningItemService(PlanningItemRepository repository, PlanningItemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // ADMIN / FORMATEUR
    public PlanningItemResponse create(PlanningItemRequest request, User creator) {
        var entity = mapper.toEntity(request, creator.getId());
        return mapper.toDto(repository.save(entity));
    }

    // ROLE-AWARE FETCH
    public List<PlanningItemResponse> getPlanningForUser(User user) {

        // Admin & Formateur see everything
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.FORMATEUR) {
            return repository.findAll()
                    .stream()
                    .map(mapper::toDto)
                    .toList();
        }

        // Bootcamper: only items where his ID is inside userIds[]
        return repository.findByUserIdsContaining(user.getId())
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public PlanningItemResponse update(String id, PlanningItemRequest request, User user) {

        PlanningItem item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planning item not found"));

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.FORMATEUR) {
            throw new RuntimeException("Forbidden");
        }

        // Only update fields that are NOT null
        if (request.title() != null)
            item.setTitle(request.title());
        if (request.description() != null)
            item.setDescription(request.description());
        if (request.type() != null)
            item.setType(request.type());
        if (request.startDate() != null)
            item.setStartDate(request.startDate());
        if (request.endDate() != null)
            item.setEndDate(request.endDate());
        if (request.userIds() != null)
            item.setUserIds(request.userIds());
        if (request.tags() != null)
            item.setTags(request.tags());

        return mapper.toDto(repository.save(item));
    }

    public void delete(String id, User user) {

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.FORMATEUR) {
            throw new RuntimeException("Forbidden");
        }

        if (!repository.existsById(id)) {
            throw new RuntimeException("Planning item not found");
        }

        repository.deleteById(id);
    }

}
