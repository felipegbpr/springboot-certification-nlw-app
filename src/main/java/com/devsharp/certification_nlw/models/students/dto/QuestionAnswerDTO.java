package com.devsharp.certification_nlw.models.students.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerDTO {

    private UUID questionID;
    private UUID alternativeID;
    private boolean isCorrect;

}
