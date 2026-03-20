package com.govqa.service;

import com.govqa.common.BizException;
import com.govqa.entity.Admin;
import com.govqa.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AdminRepository adminRepository;
    private final Map<String, LocalDateTime> tokenStore = new ConcurrentHashMap<>();

    public String login(String username, String password) {
        Admin admin = adminRepository.findByUsernameAndStatus(username, 1)
                .orElseThrow(() -> new BizException("用户名或密码错误"));
        if (!BCrypt.checkpw(password, admin.getPasswordHash())) {
            throw new BizException("用户名或密码错误");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenStore.put(token, LocalDateTime.now().plusHours(12));
        return token;
    }

    public void logout(String token) {
        tokenStore.remove(token);
    }

    public boolean isValid(String token) {
        LocalDateTime expire = tokenStore.get(token);
        return expire != null && expire.isAfter(LocalDateTime.now());
    }
}
