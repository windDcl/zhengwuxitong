package com.govqa.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NlpClient {
    private final RestTemplate restTemplate;

    @Value("${app.nlp.base-url}")
    private String nlpBaseUrl;

    @Data
    public static class MatchItem {
        private Long faqId;
        private String matchedQuestion;
        private Double score;
    }

    public List<MatchItem> match(String question, int topN) {
        Map<String, Object> req = new HashMap<>();
        req.put("question", question);
        req.put("top_n", topN);
        ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                nlpBaseUrl + "/match",
                HttpMethod.POST,
                new HttpEntity<>(req),
                new ParameterizedTypeReference<>() {
                }
        );
        Object results = resp.getBody().get("results");
        return ((List<Map<String, Object>>) results).stream().map(m -> {
            MatchItem item = new MatchItem();
            item.setFaqId(((Number) m.get("faq_id")).longValue());
            item.setMatchedQuestion((String) m.get("matched_question"));
            item.setScore(((Number) m.get("score")).doubleValue());
            return item;
        }).toList();
    }

    public void reindex(List<Map<String, Object>> faqs) {
        Map<String, Object> req = new HashMap<>();
        req.put("faqs", faqs);
        restTemplate.postForEntity(nlpBaseUrl + "/reindex", req, Map.class);
    }
}
