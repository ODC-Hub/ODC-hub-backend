package com.odc.hub.quiz.dto.responses;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionResponse {

    private String id;
    private String text;
}
