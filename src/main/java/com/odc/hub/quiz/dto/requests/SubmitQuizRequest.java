package com.odc.hub.quiz.dto.requests;

import lombok.*;

import java.util.Map;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitQuizRequest {

    /**
     * questionId -> selected optionIds
     */
    private Map<String, List<String>> answers;

    private int timeTakenSeconds;
}
