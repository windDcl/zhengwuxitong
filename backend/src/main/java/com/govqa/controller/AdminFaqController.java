package com.govqa.controller;

import com.govqa.common.ApiResponse;
import com.govqa.dto.FaqUpsertRequest;
import com.govqa.entity.Faq;
import com.govqa.service.FaqService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/faqs")
@RequiredArgsConstructor
public class AdminFaqController {
    private final FaqService faqService;

    @GetMapping
    public ApiResponse<List<Faq>> list() {
        return ApiResponse.ok(faqService.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable("id") Long id) {
        return ApiResponse.ok(faqService.detail(id));
    }

    @PostMapping
    public ApiResponse<Faq> create(@Valid @RequestBody FaqUpsertRequest req) {
        return ApiResponse.ok(faqService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<Faq> update(@PathVariable("id") Long id, @Valid @RequestBody FaqUpsertRequest req) {
        return ApiResponse.ok(faqService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        faqService.delete(id);
        return ApiResponse.ok(null);
    }

    @PostMapping("/{id}/status")
    public ApiResponse<Void> toggle(@PathVariable("id") Long id, @RequestParam("status") Integer status) {
        faqService.toggle(id, status);
        return ApiResponse.ok(null);
    }

    @PostMapping("/reindex")
    public ApiResponse<Void> reindex() {
        faqService.reindexEnabledFaqs();
        return ApiResponse.ok(null);
    }
}
