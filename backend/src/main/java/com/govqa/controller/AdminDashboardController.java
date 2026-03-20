package com.govqa.controller;

import com.govqa.common.ApiResponse;
import com.govqa.repository.CategoryRepository;
import com.govqa.repository.FaqRepository;
import com.govqa.repository.QaLogRepository;
import com.govqa.repository.UnmatchedQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final FaqRepository faqRepository;
    private final CategoryRepository categoryRepository;
    private final QaLogRepository qaLogRepository;
    private final UnmatchedQuestionRepository unmatchedQuestionRepository;

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview() {
        long todayQuestions = qaLogRepository.countByCreatedAtGreaterThanEqual(LocalDate.now().atStartOfDay());
        return ApiResponse.ok(Map.of(
                "faqCount", faqRepository.count(),
                "categoryCount", categoryRepository.count(),
                "todayQuestions", todayQuestions,
                "totalQuestions", qaLogRepository.count(),
                "unmatchedCount", unmatchedQuestionRepository.count()
        ));
    }

    @GetMapping("/trend")
    public ApiResponse<?> trend() {
        return ApiResponse.ok(qaLogRepository.trendByDay());
    }

    @GetMapping("/category-distribution")
    public ApiResponse<?> categoryDistribution() {
        return ApiResponse.ok(qaLogRepository.categoryDistribution());
    }

    @GetMapping("/hot-questions")
    public ApiResponse<?> hotQuestions() {
        return ApiResponse.ok(qaLogRepository.topHitQuestions());
    }
}
