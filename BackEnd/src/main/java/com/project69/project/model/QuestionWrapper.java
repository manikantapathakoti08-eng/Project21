package com.project69.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO sent to the client — intentionally does not include the correct answer index.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionWrapper {
    private Integer id;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
}