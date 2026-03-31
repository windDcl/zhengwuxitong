package com.govqa.service;

import com.govqa.entity.SystemSetting;
import com.govqa.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SettingService {
    private final SystemSettingRepository systemSettingRepository;

    private static final Map<String, String> DEFAULTS = new LinkedHashMap<>();

    static {
        DEFAULTS.put("semantic.threshold", "0.30");
        DEFAULTS.put("ai.enabled", "false");
        DEFAULTS.put("ai.base-url", "");
        DEFAULTS.put("ai.api-key", "");
        DEFAULTS.put("ai.model", "");
        DEFAULTS.put("ai.system-prompt", "你是一个谨慎、可靠的政务知识助手。");
    }

    public Map<String, String> getSettings() {
        Map<String, String> data = new HashMap<>(DEFAULTS);
        systemSettingRepository.findAll().forEach(item -> data.put(item.getSettingKey(), item.getSettingValue()));
        return data;
    }

    public Map<String, String> updateThreshold(String value) {
        saveSetting("semantic.threshold", value);
        return getSettings();
    }

    public Map<String, String> updateSettings(Map<String, String> values) {
        Map<String, String> sanitized = values == null ? Map.of() : values;
        for (String key : DEFAULTS.keySet()) {
            if (sanitized.containsKey(key)) {
                saveSetting(key, sanitized.get(key) == null ? "" : sanitized.get(key).trim());
            }
        }
        return getSettings();
    }

    public String get(String key) {
        return getSettings().getOrDefault(key, "");
    }

    public boolean aiEnabled() {
        return Boolean.parseBoolean(get("ai.enabled"));
    }

    public double threshold() {
        return Double.parseDouble(getSettings().get("semantic.threshold"));
    }

    private void saveSetting(String key, String value) {
        SystemSetting setting = systemSettingRepository.findBySettingKey(key).orElseGet(SystemSetting::new);
        setting.setSettingKey(key);
        setting.setSettingValue(value);
        systemSettingRepository.save(setting);
    }
}
