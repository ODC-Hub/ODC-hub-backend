package com.odc.hub.quiz.dto.responses;

import com.odc.hub.quiz.model.QuestionType;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionAdminResponse {

    private String id;
    private QuestionType type;
    private String text;

    private List<OptionResponse> options;

    private List<String> correctOptionIds;
}
