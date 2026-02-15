package com.odc.hub.ressourcemanager.repository;

import com.odc.hub.ressourcemanager.model.Livrable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LivrableRepository extends MongoRepository<Livrable, String> {

    List<Livrable> findByResourceId(String resourceId);

    Optional<Livrable> findByResourceIdAndBootcamperId(String resourceId, String bootcamperId);

    List<Livrable> findByBootcamperId(String bootcamperId);

    long countByResourceId(String resourceId);

    long countByResourceIdAndStatus(String resourceId, com.odc.hub.ressourcemanager.enums.LivrableStatus status);
}
