# 后端启动说明（T29.2）

要求：JDK 17、Maven、MySQL 可用

```bash
cd backend
mvn -q compile -Dmaven.repo.local=/tmp/m2
mvn -q org.springframework.boot:spring-boot-maven-plugin:3.3.5:run -Dmaven.repo.local=/tmp/m2
```

健康检查：
```bash
curl -s http://127.0.0.1:8080/health
```
