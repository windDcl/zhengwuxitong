package com.govqa.service;

import com.govqa.common.BizException;
import com.govqa.dto.FaqUpsertRequest;
import com.govqa.dto.UnmatchedResolveRequest;
import com.govqa.entity.Category;
import com.govqa.entity.UnmatchedQuestion;
import com.govqa.repository.CategoryRepository;
import com.govqa.repository.UnmatchedQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UnmatchedQuestionService {
    private final UnmatchedQuestionRepository unmatchedQuestionRepository;
    private final CategoryRepository categoryRepository;
    private final FaqService faqService;
    private final AiAssistService aiAssistService;

    public List<Map<String, Object>> list() {
        return unmatchedQuestionRepository.findAdminList();
    }

    public Map<String, Object> aiSuggest(Long id) {
        UnmatchedQuestion item = getById(id);
        List<String> categoryNames = categoryRepository.findAll().stream().map(Category::getName).toList();
        Map<String, Object> suggestion = aiAssistService.suggestKnowledgeCard(item.getUserQuestion(), categoryNames);
        item.setAiSuggestedQuestion(String.valueOf(suggestion.get("standardQuestion")));
        item.setAiSuggestedAnswer(String.valueOf(suggestion.get("standardAnswer")));
        item.setAiSuggestedCategory(String.valueOf(suggestion.get("categoryName")));
        item.setAiSuggestedAliases(joinAliases(suggestion.get("aliases")));
        if (item.getStatus() == null || item.getStatus() == 0) {
            item.setStatus(1);
        }
        unmatchedQuestionRepository.save(item);

        Map<String, Object> result = new HashMap<>(suggestion);
        result.put("recordId", item.getId());
        return result;
    }

    @Transactional
    public Map<String, Object> resolveToFaq(Long id, UnmatchedResolveRequest request) {
        UnmatchedQuestion item = getById(id);
        FaqUpsertRequest faqRequest = new FaqUpsertRequest();
        faqRequest.setCategoryId(request.getCategoryId());
        faqRequest.setStandardQuestion(request.getStandardQuestion());
        faqRequest.setStandardAnswer(request.getStandardAnswer());
        faqRequest.setAliases(request.getAliases());
        faqRequest.setStatus(request.getStatus());
        Long faqId = faqService.create(faqRequest).getId();

        item.setResolvedFaqId(faqId);
        item.setStatus(2);
        item.setAiSuggestedQuestion(request.getStandardQuestion());
        item.setAiSuggestedAnswer(request.getStandardAnswer());
        item.setAiSuggestedAliases(String.join("\n", request.getAliases() == null ? List.of() : request.getAliases()));
        item.setResolvedAt(LocalDateTime.now());
        unmatchedQuestionRepository.save(item);
        return Map.of("faqId", faqId, "unmatchedId", item.getId());
    }

    public void markIgnored(Long id) {
        UnmatchedQuestion item = getById(id);
        item.setStatus(3);
        item.setResolvedAt(LocalDateTime.now());
        unmatchedQuestionRepository.save(item);
    }

    private UnmatchedQuestion getById(Long id) {
        return unmatchedQuestionRepository.findById(id).orElseThrow(() -> new BizException("未命中问题不存在"));
    }

    private String joinAliases(Object aliases) {
        if (!(aliases instanceof List<?> list)) {
            return "";
        }
        List<String> values = new ArrayList<>();
        for (Object item : list) {
            if (item != null && !String.valueOf(item).isBlank()) {
                values.add(String.valueOf(item).trim());
            }
        }
        return String.join("\n", values);
    }
}
