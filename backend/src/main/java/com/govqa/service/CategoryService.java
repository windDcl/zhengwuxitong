package com.govqa.service;

import com.govqa.common.BizException;
import com.govqa.dto.CategoryUpsertRequest;
import com.govqa.entity.Category;
import com.govqa.repository.CategoryRepository;
import com.govqa.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final FaqRepository faqRepository;

    public List<Category> listAll() {
        return categoryRepository.findAll();
    }

    public List<Category> listEnabled() {
        return categoryRepository.findByStatusOrderBySortOrderAsc(1);
    }

    public Category create(CategoryUpsertRequest req) {
        categoryRepository.findByName(req.getName()).ifPresent(c -> {
            throw new BizException("分类名称已存在");
        });
        Category c = new Category();
        c.setName(req.getName());
        c.setSortOrder(req.getSortOrder());
        c.setStatus(req.getStatus());
        return categoryRepository.save(c);
    }

    public Category update(Long id, CategoryUpsertRequest req) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> new BizException("分类不存在"));
        c.setName(req.getName());
        c.setSortOrder(req.getSortOrder());
        c.setStatus(req.getStatus());
        return categoryRepository.save(c);
    }

    public void delete(Long id) {
        if (faqRepository.countByCategoryId(id) > 0) {
            throw new BizException("分类下仍有 FAQ，不能删除");
        }
        categoryRepository.deleteById(id);
    }
}
