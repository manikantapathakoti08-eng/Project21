package com.project69.project.service;

import com.project69.project.model.QuestionWrapper;
import com.project69.project.model.QuizResult;
import com.project69.project.model.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Service interface for quiz operations.
 * (Unchanged from your provided version but included here for completeness.)
 */
public interface QuizService {
    ResponseEntity<String> createQuiz(int noQ, String title);
    ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id);
    ResponseEntity<QuizResult> calculateResult(Integer id, List<Response> responses);
}