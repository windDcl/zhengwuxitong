package com.govqa.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SettingUpdateRequest {
    private Map<String, String> settings = new HashMap<>();
}
