# 电子份子钱礼簿（个人版）

一个移动端优先的微信 H5 网页应用，适合个人在农村婚礼、乔迁等场景记录电子礼金。

## 1. 项目结构

```text
gift-ledger-app/
├─ backend/                     # Spring Boot 3 + Java 17 + MySQL
│  ├─ pom.xml
│  ├─ src/main/java/com/example/giftledger
│  │  ├─ GiftLedgerApplication.java
│  │  ├─ config/
│  │  ├─ controller/
│  │  ├─ dto/
│  │  ├─ entity/
│  │  ├─ repository/
│  │  └─ service/
│  └─ src/main/resources/
│     ├─ application.yml
│     └─ static/                # 前端打包产物放这里（含 uploads）
└─ frontend/                    # Vue3 + Vite + JS + Vant3
   ├─ package.json
   ├─ vite.config.js
   └─ src/
      ├─ api/
      ├─ router/
      └─ views/
```

## 2. MySQL 建表 SQL

```sql
CREATE DATABASE IF NOT EXISTS gift_ledger DEFAULT CHARACTER SET utf8mb4;
USE gift_ledger;

CREATE TABLE IF NOT EXISTS activity (
  id BIGINT PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  date VARCHAR(30) NOT NULL,
  location VARCHAR(200) NOT NULL,
  wx_qrcode_path VARCHAR(255),
  ali_qrcode_path VARCHAR(255)
);

INSERT INTO activity(id, title, date, location, wx_qrcode_path, ali_qrcode_path)
VALUES (1, '乔迁之喜', '2026-01-01', '某某村·某某大院', NULL, NULL)
ON DUPLICATE KEY UPDATE id = id;

CREATE TABLE IF NOT EXISTS gift_records (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  submit_id VARCHAR(64) NOT NULL,
  name VARCHAR(50) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  relation VARCHAR(20) NOT NULL,
  blessing VARCHAR(255),
  payer_name VARCHAR(50) NOT NULL,
  payer_phone VARCHAR(20),
  submit_time DATETIME NOT NULL,
  ip VARCHAR(64)
);
```

## 3. application.yml 示例

`backend/src/main/resources/application.yml`：

```yml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gift_ledger?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

app:
  admin:
    username: admin
    password: admin
  upload-dir: uploads
```

## 4. 启动步骤

### 4.1 启动后端

```bash
cd backend
mvn spring-boot:run
```

### 4.2 启动前端开发环境

```bash
cd frontend
npm install
npm run dev
```

## 5. 前后端一体部署（单 JAR）

1. 先打包前端：

```bash
cd frontend
npm run build
```

2. 把 `frontend/dist` 下文件复制到 `backend/src/main/resources/static/`：

```bash
cp -r frontend/dist/* backend/src/main/resources/static/
```

3. 打包并运行后端：

```bash
cd backend
mvn clean package
java -jar target/gift-ledger-backend-1.0.0.jar
```

访问：
- 宾客页：`http://localhost:8080/`
- 后台页：`http://localhost:8080/admin`

## 6. 默认后台账号密码

- 用户名：`admin`
- 密码：`admin`

## 7. 关键能力说明

- 宾客端支持动态多行录入（1~10条）、实时总金额计算、提交后防重复。
- 后台支持登录、活动信息和收款码维护、记录搜索、按提交批次折叠、总金额统计、Excel 导出、删除/清空。
- 图片上传使用 `MultipartFile`，保存到 `backend/src/main/resources/static/uploads`，数据库保存路径如 `/uploads/xxx.jpg`。
- 防刷规则：同一 IP 在 1 分钟内最多 3 次提交。
