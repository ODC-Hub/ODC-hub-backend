package com.odc.hub.quiz.dto.responses;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResponse {

    private String id;
    private String title;
    private String module;
    private int durationSeconds;
    private int passingScore;

    private List<QuestionResponse> questions;
}
