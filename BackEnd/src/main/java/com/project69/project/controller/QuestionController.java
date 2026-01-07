package com.project69.project.controller;

import com.project69.project.model.C_BASIC;
import com.project69.project.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/allquestions")
    public ResponseEntity<List<C_BASIC>> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<C_BASIC> getQuestionById(@PathVariable Integer id) {
        return questionService.getQuestionById(id);
    }

    @PostMapping
    public ResponseEntity<C_BASIC> addQuestion(@RequestBody C_BASIC question) {
        return questionService.saveQuestion(question);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer id) {
        return questionService.deleteQuestion(id);
    }
}
