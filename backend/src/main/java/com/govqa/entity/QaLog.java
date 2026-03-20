package com.govqa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "qa_log")
public class QaLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_question")
    private String userQuestion;
    @Column(name = "matched_question")
    private String matchedQuestion;
    @Column(name = "matched_faq_id")
    private Long matchedFaqId;
    private BigDecimal similarity;
    @Column(name = "is_hit")
    private Integer isHit;
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
