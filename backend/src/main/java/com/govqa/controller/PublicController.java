package com.govqa.controller;

import com.govqa.common.ApiResponse;
import com.govqa.dto.AskRequest;
import com.govqa.entity.Announcement;
import com.govqa.entity.Category;
import com.govqa.repository.QaLogRepository;
import com.govqa.service.AnnouncementService;
import com.govqa.service.AskService;
import com.govqa.service.CategoryService;
import com.govqa.service.FaqService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {
    private final CategoryService categoryService;
    private final AnnouncementService announcementService;
    private final AskService askService;
    private final FaqService faqService;
    private final QaLogRepository qaLogRepository;

    @GetMapping("/categories")
    public ApiResponse<List<Category>> categories() {
        return ApiResponse.ok(categoryService.listEnabled());
    }

    @GetMapping("/announcements")
    public ApiResponse<List<Announcement>> announcements() {
        return ApiResponse.ok(announcementService.listPublic());
    }

    @PostMapping("/ask")
    public ApiResponse<Map<String, Object>> ask(@Valid @RequestBody AskRequest request) {
        return ApiResponse.ok(askService.ask(request.getQuestion(), request.getTopN()));
    }

    @GetMapping("/suggestions")
    public ApiResponse<List<String>> suggestions(@RequestParam("keyword") String keyword,
                                                 @RequestParam(value = "limit", defaultValue = "8") Integer limit) {
        return ApiResponse.ok(faqService.suggestions(keyword, limit));
    }

    @GetMapping("/categories/{id}/faqs")
    public ApiResponse<List<Map<String, Object>>> categoryFaqs(@PathVariable("id") Long categoryId,
                                                               @RequestParam(value = "keyword", required = false) String keyword) {
        return ApiResponse.ok(faqService.listByCategory(categoryId, keyword));
    }

    @GetMapping("/hot-questions")
    public ApiResponse<List<Map<String, Object>>> hotQuestions() {
        return ApiResponse.ok(qaLogRepository.topHitQuestions());
    }
}
