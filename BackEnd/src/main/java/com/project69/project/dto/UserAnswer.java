package com.project69.project.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
public class UserAnswer {
    @NotNull
    private Integer questionId;
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
