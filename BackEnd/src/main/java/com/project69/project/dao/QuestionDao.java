package com.project69.project.dao;

import com.project69.project.model.C_BASIC;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao extends JpaRepository<C_BASIC, Integer> {

    @Query(value = "SELECT * FROM C_BASIC ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<C_BASIC> findRandomQuestions(@Param("count") int count);
}
