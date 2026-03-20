package com.govqa.controller;

import com.govqa.common.ApiResponse;
import com.govqa.dto.AnnouncementUpsertRequest;
import com.govqa.entity.Announcement;
import com.govqa.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/announcements")
@RequiredArgsConstructor
public class AdminAnnouncementController {
    private final AnnouncementService announcementService;

    @GetMapping
    public ApiResponse<List<Announcement>> list() {
        return ApiResponse.ok(announcementService.listAll());
    }

    @PostMapping
    public ApiResponse<Announcement> create(@Valid @RequestBody AnnouncementUpsertRequest req) {
        return ApiResponse.ok(announcementService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<Announcement> update(@PathVariable("id") Long id, @Valid @RequestBody AnnouncementUpsertRequest req) {
        return ApiResponse.ok(announcementService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        announcementService.delete(id);
        return ApiResponse.ok(null);
    }
}
