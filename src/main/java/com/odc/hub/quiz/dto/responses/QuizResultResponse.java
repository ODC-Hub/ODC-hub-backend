package com.odc.hub.quiz.dto.responses;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResultResponse {

    private int score;
    private int totalQuestions;
    private int percentage;
    private boolean passed;
}
