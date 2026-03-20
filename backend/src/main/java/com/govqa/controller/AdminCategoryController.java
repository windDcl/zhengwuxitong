package com.govqa.controller;

import com.govqa.common.ApiResponse;
import com.govqa.dto.CategoryUpsertRequest;
import com.govqa.entity.Category;
import com.govqa.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<Category>> list() {
        return ApiResponse.ok(categoryService.listAll());
    }

    @PostMapping
    public ApiResponse<Category> create(@Valid @RequestBody CategoryUpsertRequest req) {
        return ApiResponse.ok(categoryService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<Category> update(@PathVariable("id") Long id, @Valid @RequestBody CategoryUpsertRequest req) {
        return ApiResponse.ok(categoryService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
        return ApiResponse.ok(null);
    }
}
