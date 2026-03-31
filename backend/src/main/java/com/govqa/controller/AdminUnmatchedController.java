package com.govqa.controller;

import com.govqa.common.ApiResponse;
import com.govqa.dto.UnmatchedResolveRequest;
import com.govqa.service.UnmatchedQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/unmatched")
@RequiredArgsConstructor
public class AdminUnmatchedController {
    private final UnmatchedQuestionService unmatchedQuestionService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        return ApiResponse.ok(unmatchedQuestionService.list());
    }

    @PostMapping("/{id}/ai-suggestion")
    public ApiResponse<Map<String, Object>> aiSuggestion(@PathVariable("id") Long id) {
        return ApiResponse.ok(unmatchedQuestionService.aiSuggest(id));
    }

    @PostMapping("/{id}/resolve-to-faq")
    public ApiResponse<Map<String, Object>> resolveToFaq(@PathVariable("id") Long id,
                                                         @Valid @RequestBody UnmatchedResolveRequest request) {
        return ApiResponse.ok(unmatchedQuestionService.resolveToFaq(id, request));
    }

    @PostMapping("/{id}/ignore")
    public ApiResponse<Void> ignore(@PathVariable("id") Long id) {
        unmatchedQuestionService.markIgnored(id);
        return ApiResponse.ok(null);
    }
}
