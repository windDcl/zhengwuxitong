package com.govqa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "unmatched_question")
public class UnmatchedQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_question")
    private String userQuestion;
    private BigDecimal similarity;
    private Integer status;
    @Column(name = "ai_suggested_question")
    private String aiSuggestedQuestion;
    @Column(name = "ai_suggested_answer")
    private String aiSuggestedAnswer;
    @Column(name = "ai_suggested_aliases")
    private String aiSuggestedAliases;
    @Column(name = "ai_suggested_category")
    private String aiSuggestedCategory;
    @Column(name = "resolved_faq_id")
    private Long resolvedFaqId;
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
