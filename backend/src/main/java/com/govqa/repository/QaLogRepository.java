package com.govqa.repository;

import com.govqa.entity.QaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface QaLogRepository extends JpaRepository<QaLog, Long> {
    long countByIsHit(Integer isHit);
    long countByCreatedAtGreaterThanEqual(LocalDateTime start);

    @Query(value = "SELECT matched_question AS question, COUNT(*) AS cnt FROM qa_log " +
            "WHERE is_hit = 1 AND matched_question IS NOT NULL " +
            "GROUP BY matched_question ORDER BY cnt DESC LIMIT 10", nativeQuery = true)
    List<Map<String, Object>> topHitQuestions();

    @Query(value = "SELECT DATE(created_at) AS day, COUNT(*) AS cnt FROM qa_log " +
            "GROUP BY DATE(created_at) ORDER BY day ASC", nativeQuery = true)
    List<Map<String, Object>> trendByDay();

    @Query(value = "SELECT c.name AS category, COUNT(*) AS cnt FROM qa_log q " +
            "JOIN faq f ON q.matched_faq_id = f.id " +
            "JOIN category c ON f.category_id = c.id " +
            "WHERE q.is_hit = 1 GROUP BY c.name ORDER BY cnt DESC", nativeQuery = true)
    List<Map<String, Object>> categoryDistribution();
}
