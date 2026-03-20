package com.govqa.controller;

import com.govqa.common.ApiResponse;
import com.govqa.entity.QaLog;
import com.govqa.entity.UnmatchedQuestion;
import com.govqa.repository.QaLogRepository;
import com.govqa.repository.UnmatchedQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminLogController {
    private final QaLogRepository qaLogRepository;
    private final UnmatchedQuestionRepository unmatchedQuestionRepository;

    @GetMapping("/qa-logs")
    public ApiResponse<List<QaLog>> logs() {
        return ApiResponse.ok(qaLogRepository.findAll());
    }

    @GetMapping("/unmatched-questions")
    public ApiResponse<List<UnmatchedQuestion>> unmatched() {
        return ApiResponse.ok(unmatchedQuestionRepository.findAll());
    }
}
