package com.govqa.repository;

import com.govqa.entity.UnmatchedQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnmatchedQuestionRepository extends JpaRepository<UnmatchedQuestion, Long> {
}
