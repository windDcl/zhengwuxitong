package com.govqa.service;

import com.govqa.client.NlpClient;
import com.govqa.entity.Faq;
import com.govqa.entity.QaLog;
import com.govqa.entity.UnmatchedQuestion;
import com.govqa.repository.FaqRepository;
import com.govqa.repository.QaLogRepository;
import com.govqa.repository.UnmatchedQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AskService {
    private final NlpClient nlpClient;
    private final FaqRepository faqRepository;
    private final QaLogRepository qaLogRepository;
    private final UnmatchedQuestionRepository unmatchedQuestionRepository;
    private final SettingService settingService;
    private final AiAssistService aiAssistService;

    public Map<String, Object> ask(String question, int topN) {
        List<NlpClient.MatchItem> matches = nlpClient.match(question, topN);
        double threshold = settingService.threshold();
        NlpClient.MatchItem top = matches.isEmpty() ? null : matches.get(0);
        Faq topFaq = top == null ? null : faqRepository.findById(top.getFaqId()).orElse(null);
        boolean hit = top != null && top.getScore() >= threshold && topFaq != null;

        Map<String, Object> result = new HashMap<>();
        result.put("hit", hit);
        result.put("threshold", threshold);
        result.put("candidates", matches);

        if (hit) {
            result.put("faq", topFaq);
            result.put("similarity", top.getScore());
            writeLog(question, top.getMatchedQuestion(), topFaq.getId(), top.getScore(), 1);
        } else {
            Optional<String> aiAnswer = aiAssistService.answerPublicQuestion(question);
            result.put("message", aiAnswer.isPresent()
                    ? "知识库未命中，以下为第三方 AI 生成的参考答复，请以官方最新信息为准。"
                    : "未找到相关问题，建议咨询政务服务大厅或拨打12345热线");
            aiAnswer.ifPresent(answer -> result.put("aiAnswer", answer));
            writeLog(question, top == null ? null : top.getMatchedQuestion(),
                    topFaq == null ? null : topFaq.getId(), top == null ? null : top.getScore(), 0);
            writeUnmatched(question, top == null ? null : top.getScore());
        }
        return result;
    }

    private void writeLog(String userQuestion, String matchedQuestion, Long faqId, Double similarity, int hit) {
        QaLog log = new QaLog();
        log.setUserQuestion(userQuestion);
        log.setMatchedQuestion(matchedQuestion);
        log.setMatchedFaqId(faqId);
        log.setSimilarity(similarity == null ? null : BigDecimal.valueOf(similarity));
        log.setIsHit(hit);
        qaLogRepository.save(log);
    }

    private void writeUnmatched(String question, Double similarity) {
        UnmatchedQuestion q = new UnmatchedQuestion();
        q.setUserQuestion(question);
        q.setSimilarity(similarity == null ? null : BigDecimal.valueOf(similarity));
        q.setStatus(0);
        unmatchedQuestionRepository.save(q);
    }
}
