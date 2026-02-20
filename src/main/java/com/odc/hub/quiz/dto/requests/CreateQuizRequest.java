package com.odc.hub.quiz.dto.requests;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuizRequest {

    private String title;
    private String module;
    private int durationSeconds;
    private int passingScore;

    private List<QuestionRequest> questions;
}
