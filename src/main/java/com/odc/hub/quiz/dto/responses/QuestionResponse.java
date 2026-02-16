package com.odc.hub.quiz.dto.responses;

import com.odc.hub.quiz.model.QuestionType;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private String id;
    private QuestionType type;
    private String text;
    private List<OptionResponse> options;
}
