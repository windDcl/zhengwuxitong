package com.govqa.service;

import com.govqa.client.NlpClient;
import com.govqa.common.BizException;
import com.govqa.dto.FaqUpsertRequest;
import com.govqa.entity.Faq;
import com.govqa.entity.FaqAlias;
import com.govqa.repository.FaqAliasRepository;
import com.govqa.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FaqService {
    private final FaqRepository faqRepository;
    private final FaqAliasRepository faqAliasRepository;
    private final NlpClient nlpClient;

    public List<Faq> list() {
        return faqRepository.findAll();
    }

    public List<String> suggestions(String keyword, int limit) {
        String k = keyword == null ? "" : keyword.trim();
        if (k.isEmpty()) {
            return List.of();
        }
        List<Faq> enabledFaqs = faqRepository.findByStatus(1);
        List<String> results = new ArrayList<>();
        for (Faq faq : enabledFaqs) {
            if (faq.getStandardQuestion() != null && faq.getStandardQuestion().contains(k)) {
                results.add(faq.getStandardQuestion());
            }
            for (FaqAlias alias : faqAliasRepository.findByFaqId(faq.getId())) {
                if (alias.getAliasQuestion() != null && alias.getAliasQuestion().contains(k)) {
                    results.add(alias.getAliasQuestion());
                }
            }
            if (results.size() >= limit) {
                break;
            }
        }
        return results.stream().distinct().limit(limit).toList();
    }

    public List<Map<String, Object>> listByCategory(Long categoryId, String keyword) {
        String k = keyword == null ? "" : keyword.trim();
        List<Faq> faqs = faqRepository.findByCategoryIdAndStatus(categoryId, 1);
        List<Map<String, Object>> results = new ArrayList<>();
        for (Faq faq : faqs) {
            List<FaqAlias> aliases = faqAliasRepository.findByFaqId(faq.getId());
            boolean matched = k.isEmpty() || faq.getStandardQuestion().contains(k)
                    || aliases.stream().anyMatch(a -> a.getAliasQuestion() != null && a.getAliasQuestion().contains(k));
            if (!matched) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("id", faq.getId());
            item.put("standardQuestion", faq.getStandardQuestion());
            item.put("standardAnswer", faq.getStandardAnswer());
            item.put("aliases", aliases.stream().map(FaqAlias::getAliasQuestion).toList());
            results.add(item);
        }
        return results;
    }

    public Map<String, Object> detail(Long id) {
        Faq faq = faqRepository.findById(id).orElseThrow(() -> new BizException("FAQ 不存在"));
        List<FaqAlias> aliases = faqAliasRepository.findByFaqId(id);
        Map<String, Object> data = new HashMap<>();
        data.put("faq", faq);
        data.put("aliases", aliases);
        return data;
    }

    @Transactional
    public Faq create(FaqUpsertRequest req) {
        Faq faq = new Faq();
        apply(req, faq);
        Faq saved = faqRepository.save(faq);
        saveAliases(saved.getId(), req.getAliases());
        reindexEnabledFaqs();
        return saved;
    }

    @Transactional
    public Faq update(Long id, FaqUpsertRequest req) {
        Faq faq = faqRepository.findById(id).orElseThrow(() -> new BizException("FAQ 不存在"));
        apply(req, faq);
        Faq saved = faqRepository.save(faq);
        faqAliasRepository.deleteByFaqId(id);
        saveAliases(id, req.getAliases());
        reindexEnabledFaqs();
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        faqAliasRepository.deleteByFaqId(id);
        faqRepository.deleteById(id);
        reindexEnabledFaqs();
    }

    @Transactional
    public void toggle(Long id, Integer status) {
        Faq faq = faqRepository.findById(id).orElseThrow(() -> new BizException("FAQ 不存在"));
        faq.setStatus(status);
        faqRepository.save(faq);
        reindexEnabledFaqs();
    }

    public List<Map<String, Object>> enabledFaqForNlp() {
        List<Faq> faqs = faqRepository.findByStatus(1);
        List<Map<String, Object>> payload = new ArrayList<>();
        for (Faq faq : faqs) {
            List<String> texts = new ArrayList<>();
            texts.add(faq.getStandardQuestion());
            texts.addAll(faqAliasRepository.findByFaqId(faq.getId()).stream().map(FaqAlias::getAliasQuestion).toList());
            Map<String, Object> item = new HashMap<>();
            item.put("faq_id", faq.getId());
            item.put("standard_question", faq.getStandardQuestion());
            item.put("texts", texts);
            payload.add(item);
        }
        return payload;
    }

    public void reindexEnabledFaqs() {
        nlpClient.reindex(enabledFaqForNlp());
    }

    private void apply(FaqUpsertRequest req, Faq faq) {
        faq.setCategoryId(req.getCategoryId());
        faq.setStandardQuestion(req.getStandardQuestion());
        faq.setStandardAnswer(req.getStandardAnswer());
        faq.setStatus(req.getStatus());
    }

    private void saveAliases(Long faqId, List<String> aliases) {
        if (aliases == null) {
            return;
        }
        for (String alias : aliases) {
            if (alias == null || alias.isBlank()) {
                continue;
            }
            FaqAlias a = new FaqAlias();
            a.setFaqId(faqId);
            a.setAliasQuestion(alias.trim());
            faqAliasRepository.save(a);
        }
    }
}
