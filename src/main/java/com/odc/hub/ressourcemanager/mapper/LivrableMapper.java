package com.odc.hub.ressourcemanager.mapper;

import com.odc.hub.ressourcemanager.dto.LivrableResponse;
import com.odc.hub.ressourcemanager.model.Livrable;
import org.springframework.stereotype.Component;

@Component
public class LivrableMapper {

    public LivrableResponse toResponse(Livrable livrable) {
        LivrableResponse response = new LivrableResponse();
        response.setId(livrable.getId());
        response.setResourceId(livrable.getResourceId());
        response.setBootcamperId(livrable.getBootcamperId());
        response.setStatus(livrable.getStatus());
        response.setReviewerComment(livrable.getReviewerComment());
        response.setSubmittedAt(livrable.getSubmittedAt());
        return response;
    }
}
