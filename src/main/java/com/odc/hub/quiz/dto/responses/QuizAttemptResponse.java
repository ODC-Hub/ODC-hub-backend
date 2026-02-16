package com.odc.hub.quiz.dto.responses;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptResponse {

    private String quizId;
    private String quizTitle;
    private String module;

    private String bootcamperId;
    private String bootcamperName;

    private int score;
    private int totalQuestions;
    private int percentage;
    private boolean passed;

    private int timeTakenSeconds;
    private Instant submittedAt;
}
