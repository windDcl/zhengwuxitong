package com.govqa.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.govqa.common.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAssistService {
    private final RestTemplate restTemplate;
    private final SettingService settingService;
    private final ObjectMapper objectMapper;

    public Map<String, Object> suggestKnowledgeCard(String userQuestion, List<String> categoryNames) {
        ensureEnabled();
        String categories = categoryNames == null || categoryNames.isEmpty()
                ? "未提供分类"
                : String.join("、", categoryNames);
        String prompt = """
                你是政务知识库运营助手。请根据用户提问，生成一份适合录入 FAQ 知识库的建议结果。
                要求：
                1. 输出 JSON，不要输出 markdown。
                2. JSON 字段固定为 standardQuestion, standardAnswer, aliases, categoryName, handlingAdvice。
                3. aliases 为字符串数组，最多 5 个。
                4. categoryName 优先从以下分类中选择最合适的一项：%s。
                5. standardAnswer 要专业、克制，不能编造具体政策编号；如不确定，请明确提示需以当地窗口和官方公告为准。
                用户问题：%s
                """.formatted(categories, userQuestion);
        Map<String, Object> parsed = parseJsonContent(callChat(prompt, true));
        normalizeSuggestion(parsed, userQuestion);
        return parsed;
    }

    public Optional<String> answerPublicQuestion(String userQuestion) {
        if (!settingService.aiEnabled()) {
            return Optional.empty();
        }
        String prompt = """
                你是政务问答助手。请直接回答用户问题。
                要求：
                1. 回答使用简体中文，语气清晰、可靠。
                2. 若政策细节可能因地区和时间变化而不同，请提醒用户以当地政务大厅、12345 或官方公告为准。
                3. 不要使用 markdown 标题，不要编造政策文号。
                用户问题：%s
                """.formatted(userQuestion);
        try {
            String content = callChat(prompt, false).trim();
            return content.isBlank() ? Optional.empty() : Optional.of(content);
        } catch (Exception ex) {
            log.warn("AI public answer failed for question={}", userQuestion, ex);
            return Optional.empty();
        }
    }

    private void normalizeSuggestion(Map<String, Object> parsed, String userQuestion) {
        parsed.putIfAbsent("standardQuestion", userQuestion);
        parsed.putIfAbsent("standardAnswer", "建议联系当地政务服务大厅或拨打 12345 进一步核实。");
        parsed.putIfAbsent("categoryName", "");
        parsed.putIfAbsent("handlingAdvice", "请由管理员复核后再录入知识库。");
        Object aliases = parsed.get("aliases");
        if (!(aliases instanceof List<?>)) {
            parsed.put("aliases", new ArrayList<String>());
        }
    }

    private void ensureEnabled() {
        if (!settingService.aiEnabled()) {
            throw new BizException("第三方 AI 未启用，请先在系统设置中完成配置");
        }
    }

    private Map<String, Object> parseJsonContent(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<>() {});
        } catch (Exception ex) {
            throw new BizException("AI 返回结果无法解析，请调整模型提示词后重试");
        }
    }

    private String callChat(String prompt, boolean expectJson) {
        String baseUrl = settingService.get("ai.base-url");
        String apiKey = settingService.get("ai.api-key");
        String model = settingService.get("ai.model");
        if (baseUrl.isBlank() || apiKey.isBlank() || model.isBlank()) {
            throw new BizException("第三方 AI 配置不完整，请检查 base URL、API Key 和模型名称");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("temperature", 0.3);
        if (expectJson) {
            body.put("response_format", Map.of("type", "json_object"));
        }
        body.put("messages", List.of(
                Map.of("role", "system", "content", settingService.get("ai.system-prompt")),
                Map.of("role", "user", "content", prompt)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                appendChatPath(baseUrl),
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                new org.springframework.core.ParameterizedTypeReference<>() {}
        );
        Object choices = response.getBody() == null ? null : response.getBody().get("choices");
        if (!(choices instanceof List<?> choiceList) || choiceList.isEmpty()) {
            throw new BizException("第三方 AI 未返回有效结果");
        }
        Object message = ((Map<?, ?>) choiceList.get(0)).get("message");
        if (!(message instanceof Map<?, ?> messageMap) || messageMap.get("content") == null) {
            throw new BizException("第三方 AI 响应格式异常");
        }
        return String.valueOf(messageMap.get("content"));
    }

    private String appendChatPath(String baseUrl) {
        String normalized = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return normalized.endsWith("/chat/completions") ? normalized : normalized + "/chat/completions";
    }
}
