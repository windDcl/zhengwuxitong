package com.govqa.repository;

import com.govqa.entity.FaqAlias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqAliasRepository extends JpaRepository<FaqAlias, Long> {
    List<FaqAlias> findByFaqId(Long faqId);
    void deleteByFaqId(Long faqId);
}
