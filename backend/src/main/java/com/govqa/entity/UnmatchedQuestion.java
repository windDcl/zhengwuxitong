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
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
