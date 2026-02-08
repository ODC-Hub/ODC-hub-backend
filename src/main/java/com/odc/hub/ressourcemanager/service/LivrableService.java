package com.odc.hub.ressourcemanager.service;

import com.odc.hub.ressourcemanager.dto.LivrableCreateRequest;
import com.odc.hub.ressourcemanager.dto.LivrableResponse;
import com.odc.hub.ressourcemanager.enums.LivrableStatus;
import com.odc.hub.ressourcemanager.exception.NotFoundException;
import com.odc.hub.ressourcemanager.mapper.LivrableMapper;
import com.odc.hub.ressourcemanager.model.Livrable;
import com.odc.hub.ressourcemanager.repository.LivrableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LivrableService {

    private final LivrableRepository livrableRepository;
    private final LivrableMapper livrableMapper;
    private final GridFsService gridFsService;

    public LivrableResponse submitLivrable(
            LivrableCreateRequest request,
            MultipartFile file,
            String bootcamperId
    ) {
        livrableRepository
                .findByResourceIdAndBootcamperId(request.getResourceId(), bootcamperId)
                .ifPresent(l -> {
                    throw new RuntimeException("Livrable already submitted");
                });

        String fileId = gridFsService.uploadFile(file);

        Livrable livrable = Livrable.builder()
                .resourceId(request.getResourceId())
                .bootcamperId(bootcamperId)
                .gridFsFileId(fileId)
                .build();

        return livrableMapper.toResponse(
                livrableRepository.save(livrable)
        );
    }

    public LivrableResponse reviewLivrable(
            String livrableId,
            LivrableStatus status,
            String comment,
            String reviewerId
    ) {
        Livrable livrable = livrableRepository.findById(livrableId)
                .orElseThrow(() -> new NotFoundException("Livrable not found"));

        livrable.setStatus(status);
        livrable.setReviewerComment(comment);
        livrable.setReviewedBy(reviewerId);

        return livrableMapper.toResponse(
                livrableRepository.save(livrable)
        );
    }
}
