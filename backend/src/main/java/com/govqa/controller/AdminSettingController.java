package com.govqa.controller;

import com.govqa.common.ApiResponse;
import com.govqa.dto.SettingUpdateRequest;
import com.govqa.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
public class AdminSettingController {
    private final SettingService settingService;

    @GetMapping
    public ApiResponse<Map<String, String>> getSettings() {
        return ApiResponse.ok(settingService.getSettings());
    }

    @PutMapping("/threshold")
    public ApiResponse<Map<String, String>> updateThreshold(@RequestParam("value") String value) {
        return ApiResponse.ok(settingService.updateThreshold(value));
    }

    @PutMapping
    public ApiResponse<Map<String, String>> updateSettings(@RequestBody SettingUpdateRequest request) {
        return ApiResponse.ok(settingService.updateSettings(request.getSettings()));
    }
}
