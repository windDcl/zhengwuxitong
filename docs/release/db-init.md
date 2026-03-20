# 数据库初始化说明（T29.1）

```bash
mysql -h127.0.0.1 -P3306 -uroot -p12345678 < sql/init.sql
```

初始化后检查：
```sql
USE gov_qa;
SHOW TABLES;
SELECT COUNT(*) FROM category;
SELECT COUNT(*) FROM admin;
```
