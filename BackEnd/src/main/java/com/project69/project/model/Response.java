package com.project69.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Internal model representing a user's response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    @NotNull
    private Integer questionId;

    // allow null (skipped) — Min/Max validate when value is present
    @Min(0)
    @Max(3)
    private Integer responseIndex;
}