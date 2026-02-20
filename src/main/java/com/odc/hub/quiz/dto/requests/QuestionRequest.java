package com.odc.hub.quiz.dto.requests;

import com.odc.hub.quiz.model.QuestionType;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequest {

    private String text;
    private QuestionType type;
    private List<OptionRequest> options;

    /**
     * Always list (single or multiple)
     */
    private List<String> correctOptionIds;
}
