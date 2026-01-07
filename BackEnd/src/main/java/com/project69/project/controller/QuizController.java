package com.project69.project.controller;

import com.project69.project.dto.UserAnswer;
import com.project69.project.dto.QuizRequest;
import com.project69.project.model.QuestionWrapper;
import com.project69.project.model.Response;
import com.project69.project.model.QuizResult;
import com.project69.project.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/quiz")
@Validated
public class QuizController {

    private static final Logger log = LoggerFactory.getLogger(QuizController.class);

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createQuiz(
            @RequestParam(required = false) Integer noQ,
            @RequestParam(required = false) String title,
            @RequestBody(required = false) QuizRequest request) {

        if (request != null) {
            return quizService.createQuiz(request.getNoQ(), request.getTitle());
        }

        if (noQ != null && title != null) {
            return quizService.createQuiz(noQ, title);
        }

        return ResponseEntity.badRequest().body("Missing parameters: provide either query params or JSON body");
    }

    @GetMapping(value = "/get/{id}", produces = "application/json")
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(@PathVariable Integer id) {
        return quizService.getQuizQuestions(id);
    }

    @PostMapping(value = "/submit/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<QuizResult> submitQuiz(
            @PathVariable Integer id,
            @RequestBody @Valid List<@Valid UserAnswer> answers) {

        log.debug("submitQuiz called for quizId={} with answersCount={}", id, answers == null ? 0 : answers.size());

        if (answers == null || answers.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ResponseEntity<List<QuestionWrapper>> quizQuestionsResp = quizService.getQuizQuestions(id);
        if (!quizQuestionsResp.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(quizQuestionsResp.getStatusCode()).build();
        }

        List<QuestionWrapper> questionWrappers = quizQuestionsResp.getBody();
        if (questionWrappers == null) {
            return ResponseEntity.status(500).build();
        }

        Set<Integer> validQuestionIds = questionWrappers.stream()
                .map(QuestionWrapper::getId)
                .collect(Collectors.toSet());

        for (UserAnswer ua : answers) {
            if (ua.getQuestionId() == null) {
                log.warn("Submission contains null questionId for quizId={}", id);
                return ResponseEntity.badRequest().build();
            }
            if (!validQuestionIds.contains(ua.getQuestionId())) {
                log.warn("Submission contains invalid questionId={} for quizId={}", ua.getQuestionId(), id);
                return ResponseEntity.badRequest().build();
            }
        }

        if (answers.size() > questionWrappers.size()) {
            log.warn("Submission for quizId={} has more answers ({}) than questions ({})", id, answers.size(), questionWrappers.size());
            return ResponseEntity.badRequest().build();
        }

        List<Response> modelResponses = answers.stream().map(ua -> {
            Response r = new Response();
            r.setQuestionId(ua.getQuestionId());
            r.setResponseIndex(ua.getResponseIndex());
            return r;
        }).collect(Collectors.toList());

        return quizService.calculateResult(id, modelResponses);
    }
}