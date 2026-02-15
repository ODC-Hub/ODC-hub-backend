package com.odc.hub.ressourcemanager.mapper;

import com.odc.hub.ressourcemanager.dto.LivrableResponse;
import com.odc.hub.ressourcemanager.model.Livrable;
import com.odc.hub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LivrableMapper {

    private final UserRepository userRepository;

    public LivrableResponse toResponse(Livrable livrable) {
        LivrableResponse response = new LivrableResponse();
        response.setId(livrable.getId());
        response.setResourceId(livrable.getResourceId());
        response.setBootcamperId(livrable.getBootcamperId());

        response.setStatus(livrable.getStatus());
        response.setReviewerComment(livrable.getReviewerComment());
        response.setStudentComment(livrable.getStudentComment());
        response.setSubmittedAt(livrable.getSubmittedAt());

        response.setFileId(livrable.getGridFsFileId());
        response.setFilename(livrable.getFilename());

        userRepository.findById(livrable.getBootcamperId())
                .ifPresentOrElse(
                        user -> {
                            String name = user.getFullName();
                            response.setBootcamperName(
                                    (name != null && !name.trim().isEmpty()) ? name : user.getEmail());
                        },
                        () -> response.setBootcamperName("Unknown Student"));

        return response;
    }
}
