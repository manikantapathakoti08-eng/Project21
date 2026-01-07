package com.project69.project.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing a single submitted answer from the client.
 */
public class UserAnswer {

    @NotNull
    private Integer questionId;

    // allow null (skipped). If present, must be 0..3
    @Min(0)
    @Max(3)
    private Integer responseIndex;

    public UserAnswer() {}

    public UserAnswer(Integer questionId, Integer responseIndex) {
        this.questionId = questionId;
        this.responseIndex = responseIndex;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getResponseIndex() {
        return responseIndex;
    }

    public void setResponseIndex(Integer responseIndex) {
        this.responseIndex = responseIndex;
    }
}