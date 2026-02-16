package com.odc.hub.quiz.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "quiz_attempts")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptDocument {

    @Id
    private String id;

    private String quizId;
    private String userId;

    private int score;
    private int totalQuestions;

    private int percentage;

    private boolean passed;

    private int timeTakenSeconds;

    private Instant submittedAt;
}
