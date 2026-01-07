package com.project69.project.service;

import com.project69.project.dao.QuestionDao;
import com.project69.project.model.C_BASIC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing C_BASIC questions.
 *
 * Notes:
 * - Methods return ResponseEntity to be compatible with your current controllers.
 * - Prefer moving HTTP concerns to controllers and returning domain objects/DTOs from services
 *   in the medium/long term.
 */
@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    private final QuestionDao questionDao;

    public QuestionService(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    /**
     * Get all questions. Marked readOnly to optimize transaction handling.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<List<C_BASIC>> getAllQuestions() {
        try {
            List<C_BASIC> questions = questionDao.findAll();
            if (questions == null || questions.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            log.error("Failed to fetch all questions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    /**
     * Get a single question by ID.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<C_BASIC> getQuestionById(Integer id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Optional<C_BASIC> opt = questionDao.findById(id);
            return opt.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Failed to fetch question with id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Save a new question (or update an existing one).
     * Marked transactional since it modifies data.
     */
    @Transactional
    public ResponseEntity<C_BASIC> saveQuestion(C_BASIC question) {
        if (question == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            C_BASIC saved = questionDao.save(question);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("Failed to save question", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a question by id.
     */
    @Transactional
    public ResponseEntity<Void> deleteQuestion(Integer id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            if (!questionDao.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            questionDao.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete question with id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}