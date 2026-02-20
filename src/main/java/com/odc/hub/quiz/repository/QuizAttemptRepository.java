package com.odc.hub.quiz.repository;

import com.odc.hub.quiz.model.QuizAttemptDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository
        extends MongoRepository<QuizAttemptDocument, String> {

    List<QuizAttemptDocument> findByQuizId(String quizId);

    List<QuizAttemptDocument> findByUserId(String userId);

    Optional<QuizAttemptDocument> findByQuizIdAndUserId(
            String quizId,
            String userId
    );

    List<QuizAttemptDocument> findByQuizIdIn(List<String> quizIds);

    boolean existsByQuizId(String quizId);

}
