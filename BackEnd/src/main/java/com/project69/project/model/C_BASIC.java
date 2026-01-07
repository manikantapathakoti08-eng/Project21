package com.project69.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "C_BASIC")
public class C_BASIC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String question;

    @NotBlank
    @Column(nullable = false)
    private String option1;

    @NotBlank
    @Column(nullable = false)
    private String option2;

    @NotBlank
    @Column(nullable = false)
    private String option3;

    @NotBlank
    @Column(nullable = false)
    private String option4;

    @NotNull
    @Min(0)
    @Max(3)
    @Column(name = "correct_option_index", nullable = false)
    private Integer correctAnswerIndex;
}