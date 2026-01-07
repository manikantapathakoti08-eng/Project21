package com.project69.project.dao;

import com.project69.project.model.Quiz;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizDao extends JpaRepository<Quiz, Integer> {

    // Ensure we always fetch questions with the quiz to avoid lazy loading issues
    @EntityGraph(attributePaths = "questions")
    Optional<Quiz> findById(Integer id);
}