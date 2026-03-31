package com.govqa.repository;

import com.govqa.entity.UnmatchedQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface UnmatchedQuestionRepository extends JpaRepository<UnmatchedQuestion, Long> {
    @Query(value = "SELECT u.id, u.user_question AS userQuestion, u.similarity, u.status, " +
            "u.ai_suggested_question AS aiSuggestedQuestion, u.ai_suggested_answer AS aiSuggestedAnswer, " +
            "u.ai_suggested_aliases AS aiSuggestedAliases, u.ai_suggested_category AS aiSuggestedCategory, " +
            "u.resolved_faq_id AS resolvedFaqId, u.resolved_at AS resolvedAt, u.created_at AS createdAt " +
            "FROM unmatched_question u ORDER BY u.created_at DESC LIMIT 200", nativeQuery = true)
    List<Map<String, Object>> findAdminList();
}
