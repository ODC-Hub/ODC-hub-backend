package com.odc.hub.quiz.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionRequest {
    private String id;
    private String text;
}
