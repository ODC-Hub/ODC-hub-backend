package com.odc.hub.ressourcemanager.service;

import com.odc.hub.ressourcemanager.dto.LivrableCreateRequest;
import com.odc.hub.ressourcemanager.dto.LivrableResponse;
import com.odc.hub.ressourcemanager.enums.LivrableStatus;
import com.odc.hub.ressourcemanager.exception.NotFoundException;
import com.odc.hub.ressourcemanager.mapper.LivrableMapper;
import com.odc.hub.ressourcemanager.model.Livrable;
import com.odc.hub.ressourcemanager.repository.LivrableRepository;
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

        public LivrableResponse submitLivrable(
                        LivrableCreateRequest request,
                        MultipartFile file,
                        String userEmail) {
                // Get user by email (email comes from Principal.getName())
                com.odc.hub.user.model.User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                log.debug("User found: {} (ID: {})", userEmail, user.getId());

                // Check if livrable already exists
                log.debug("Checking if livrable exists for resource: {} and bootcamper: {}",
                                request.getResourceId(), user.getId());

                livrableRepository.findByResourceIdAndBootcamperId(request.getResourceId(), user.getId())
                                .ifPresent(l -> {
                                        log.warn("Livrable already submitted for resource: {} by user: {}",
                                                        request.getResourceId(), user.getId());
                                        throw new RuntimeException("Livrable already submitted");
                                });

                log.debug("Uploading file: {}", file.getOriginalFilename());
                String fileId = gridFsService.uploadFile(file);
                log.debug("File uploaded successfully, ID: {}", fileId);

                Livrable livrable = Livrable.builder()
                                .resourceId(request.getResourceId())
                                .bootcamperId(user.getId()) // Use actual user ID, not email
                                .gridFsFileId(fileId)
                                .filename(file.getOriginalFilename())
                                .studentComment(request.getStudentComment())
                                .build();

                return livrableMapper.toResponse(livrableRepository.save(livrable));
        }

        public LivrableResponse reviewLivrable(String livrableId, LivrableStatus status, String comment,
                        String reviewerId) {
                Livrable livrable = livrableRepository.findById(livrableId)
                                .orElseThrow(() -> new NotFoundException("Livrable not found"));
                livrable.setStatus(status);
                livrable.setReviewerComment(comment);
                livrable.setReviewedBy(reviewerId);
                return livrableMapper.toResponse(livrableRepository.save(livrable));
        }

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
                                .orElse(java.util.Collections.emptyList());
        }
}
