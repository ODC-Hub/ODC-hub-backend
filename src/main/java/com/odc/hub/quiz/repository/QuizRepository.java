package com.odc.hub.quiz.repository;

import com.odc.hub.quiz.model.QuizDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuizRepository extends MongoRepository<QuizDocument, String> {

    List<QuizDocument> findByModule(String module);

    List<QuizDocument> findByCreatedBy(String createdBy);
}
