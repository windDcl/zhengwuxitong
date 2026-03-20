package com.govqa.repository;

import com.govqa.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    long countByCategoryId(Long categoryId);
    List<Faq> findByStatus(Integer status);
    List<Faq> findByCategoryIdAndStatus(Long categoryId, Integer status);
}
