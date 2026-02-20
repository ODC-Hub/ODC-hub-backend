package com.odc.hub.quiz.model;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    private String id;

    private QuestionType type;

    private String text;

    private List<Option> options;

    private List<String> correctOptionIds;
}
