package com.govqa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "faq_alias")
public class FaqAlias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "faq_id")
    private Long faqId;
    @Column(name = "alias_question")
    private String aliasQuestion;
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
