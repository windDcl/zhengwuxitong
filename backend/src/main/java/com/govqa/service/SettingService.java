package com.govqa.service;

import com.govqa.entity.SystemSetting;
import com.govqa.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SettingService {
    private final SystemSettingRepository systemSettingRepository;

    public Map<String, String> getSettings() {
        Map<String, String> data = new HashMap<>();
        String threshold = systemSettingRepository.findBySettingKey("semantic.threshold")
                .map(SystemSetting::getSettingValue)
                .orElse("0.75");
        data.put("semantic.threshold", threshold);
        return data;
    }

    public Map<String, String> updateThreshold(String value) {
        SystemSetting setting = systemSettingRepository.findBySettingKey("semantic.threshold").orElseGet(SystemSetting::new);
        setting.setSettingKey("semantic.threshold");
        setting.setSettingValue(value);
        systemSettingRepository.save(setting);
        return getSettings();
    }

    public double threshold() {
        return Double.parseDouble(getSettings().get("semantic.threshold"));
    }
}
