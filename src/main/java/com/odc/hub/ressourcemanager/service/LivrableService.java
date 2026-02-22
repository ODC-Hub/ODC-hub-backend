package com.odc.hub.ressourcemanager.service;

import com.odc.hub.ressourcemanager.dto.LivrableCreateRequest;
import com.odc.hub.ressourcemanager.dto.LivrableResponse;
import com.odc.hub.ressourcemanager.enums.LivrableStatus;
import com.odc.hub.ressourcemanager.exception.NotFoundException;
import com.odc.hub.ressourcemanager.mapper.LivrableMapper;
import com.odc.hub.ressourcemanager.model.Livrable;
import com.odc.hub.ressourcemanager.model.Resource;
import com.odc.hub.ressourcemanager.repository.LivrableRepository;
import com.odc.hub.ressourcemanager.repository.ResourceRepository;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LivrableService {

        private final LivrableRepository livrableRepository;
        private final LivrableMapper livrableMapper;
        private final GridFsService gridFsService;
        private final UserRepository userRepository;
        private final ResourceRepository resourceRepository;
        private final ResourceNotificationService resourceNotificationService;

        // =========================
        // SUBMIT
        // =========================
        public LivrableResponse submitLivrable(
                LivrableCreateRequest request,
                MultipartFile file,
                String userEmail
        ) {
                User bootcamper = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                livrableRepository.findByResourceIdAndBootcamperId(
                        request.getResourceId(),
                        bootcamper.getId()
                ).ifPresent(l -> {
                        throw new RuntimeException("Livrable already submitted");
                });

                Livrable livrable = Livrable.builder()
                        .resourceId(request.getResourceId())
                        .bootcamperId(bootcamper.getId())
                        .gridFsFileId(gridFsService.uploadFile(file))
                        .filename(file.getOriginalFilename())
                        .studentComment(request.getStudentComment())
                        .build();

                Livrable saved = livrableRepository.save(livrable);

                Resource resource = resourceRepository.findById(request.getResourceId())
                        .orElseThrow(() -> new RuntimeException("Resource not found"));

                resourceNotificationService.onHomeworkSubmitted(resource, saved, bootcamper);

                return livrableMapper.toResponse(saved);
        }

        // =========================
        // REVIEW
        // =========================
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

                Livrable saved = livrableRepository.save(livrable);

                Resource resource = resourceRepository.findById(saved.getResourceId())
                        .orElseThrow(() -> new RuntimeException("Resource not found"));

                User reviewer = userRepository.findById(reviewerId)
                        .orElseThrow(() -> new RuntimeException("Reviewer not found"));

                resourceNotificationService.onHomeworkReviewed(resource, saved, reviewer);

                return livrableMapper.toResponse(saved);
        }

        // =========================
        // READ (UNCHANGED)
        // =========================
        public List<LivrableResponse> getLivrablesByResource(String resourceId) {
                return livrableRepository.findByResourceId(resourceId).stream()
                        .map(livrableMapper::toResponse)
                        .collect(Collectors.toList());
        }

        public List<LivrableResponse> getMyLivrables(String userEmail) {
                return userRepository.findByEmail(userEmail)
                        .map(user -> livrableRepository.findByBootcamperId(user.getId()).stream()
                                .map(livrableMapper::toResponse)
                                .collect(Collectors.toList()))
                        .orElse(List.of());
        }
}