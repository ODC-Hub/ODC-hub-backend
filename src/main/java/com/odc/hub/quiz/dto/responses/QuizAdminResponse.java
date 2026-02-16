package com.odc.hub.quiz.dto.responses;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAdminResponse {

    private String id;
    private String title;
    private String module;
    private int durationSeconds;
    private int passingScore;

    private List<QuestionAdminResponse> questions;

    private Instant createdAt;
    private boolean editable;

}
