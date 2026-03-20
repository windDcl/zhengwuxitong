package com.govqa.controller;

import com.govqa.common.ApiResponse;
import com.govqa.dto.FaqUpsertRequest;
import com.govqa.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/import")
@RequiredArgsConstructor
public class AdminImportController {
    private final FaqService faqService;
    private final DataFormatter formatter = new DataFormatter();

    @PostMapping("/faq-excel")
    public ApiResponse<Map<String, Object>> importExcel(@RequestParam("file") MultipartFile file,
                                                         @RequestParam("categoryId") Long categoryId) throws Exception {
        int success = 0;
        List<String> errors = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                try {
                    String q = getCell(row, 1);
                    String a1 = getCell(row, 2);
                    String a2 = getCell(row, 3);
                    String a3 = getCell(row, 4);
                    String answer = getCell(row, 5);
                    String status = getCell(row, 6);
                    if (q.isBlank() || answer.isBlank()) {
                        throw new IllegalArgumentException("标准问题或答案为空");
                    }
                    FaqUpsertRequest req = new FaqUpsertRequest();
                    req.setCategoryId(categoryId);
                    req.setStandardQuestion(q);
                    req.setStandardAnswer(answer);
                    req.setStatus("停用".equals(status) ? 0 : 1);
                    List<String> aliases = new ArrayList<>();
                    if (!a1.isBlank()) aliases.add(a1);
                    if (!a2.isBlank()) aliases.add(a2);
                    if (!a3.isBlank()) aliases.add(a3);
                    req.setAliases(aliases);
                    faqService.create(req);
                    success++;
                } catch (Exception ex) {
                    errors.add("第" + (i + 1) + "行: " + ex.getMessage());
                }
            }
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("successCount", success);
        resp.put("errors", errors);
        return ApiResponse.ok(resp);
    }

    private String getCell(Row row, int idx) {
        if (row.getCell(idx) == null) {
            return "";
        }
        return formatter.formatCellValue(row.getCell(idx)).trim();
    }
}
