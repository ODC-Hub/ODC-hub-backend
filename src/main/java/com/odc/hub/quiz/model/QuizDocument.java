package com.odc.hub.quiz.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "quizzes")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizDocument {

    @Id
    private String id;

    private String title;
    private String module;

    private int durationSeconds;

    private int passingScore;

    private List<Question> questions;

    private String createdBy;
    private Instant createdAt;
}
