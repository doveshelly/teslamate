# Teslamate 微信小程序 & 后端服务（简易版毛豆随行）

本项目是一个基于 [Teslamate](https://www.teslamate.com.cn/) 数据源的微信小程序，旨在提供一个方便的移动端界面来查看您的特斯拉车辆数据。它由一个 Spring Boot 后端服务和一个微信小程序前端组成。

## 架构概览

```
+-----------+      +----------------+      +---------------------+      +---------------+
| Teslamate | ---> | PostgreSQL DB  | <--- | Spring Boot Backend | <--- | 微信小程序    |
+-----------+      +----------------+      +---------------------+      +---------------+
  (数据采集)           (数据存储)                 (API 服务)                  (用户界面)
```

- **Teslamate**: 负责从您的特斯拉车辆收集详细数据。
- **PostgreSQL 数据库**: Teslamate 使用的数据库，存储所有车辆数据。
- **Spring Boot 后端**: 连接到 Teslamate 的数据库，提供数据查询和处理的 API 接口。
- **微信小程序**: 前端界面，调用后端 API，向用户展示数据。

## 部署步骤

部署此服务需要您具备一定的服务器和软件操作知识。请严格按照以下步骤进行。

### 第一步：部署 Teslamate

Teslamate 是整个服务的数据基础，必须首先部署。推荐使用 Docker 进行部署，方便快捷。

1.  **准备云服务器**: 您需要一台可以访问外网的云服务器。
2.  **安装 Docker 和 Docker-Compose**: 请确保您的服务器上已安装最新版本的 Docker 和 Docker-Compose。
3.  **部署 Teslamate**:
    * 参考 Teslamate 官方中文文档的云服务器部署指南：[https://www.teslamate.com.cn/docs/installation/auto](https://www.teslamate.com.cn/docs/installation/auto)
    * 下载 `docker-compose.yml` 文件。
    * **关键修改**: 默认情况下，Teslamate 的数据库端口不对外暴露。您需要修改 `docker-compose.yml` 文件，将 PostgreSQL 数据库的端口映射到服务器上。找到 `database` 服务部分，在 `ports` 下添加端口映射，例如：
        ```yaml
        # docker-compose.yml

        services:
          database:
            image: postgres:15
            restart: always
            environment:
              - POSTGRES_USER=teslamate
              - POSTGRES_PASSWORD=your_secret_password # 请修改为您自己的安全密码
              - POSTGRES_DB=teslamate
            volumes:
              - teslamate-db:/var/lib/postgresql/data
            ports:
              - 5432:5432
              # !!! 将下面这行取消注释或添加，以允许外部访问 !!!（如果源文件没有这行就不管）
              # - "0.0.0.0:5432:5432" # 将 5432 端口暴露到服务器的所有网络接口
        ```
    > **安全警告**: 将数据库端口暴露到公网存在安全风险。请务必配置复杂的数据库密码，并设置服务器防火墙规则，仅允许您的后端服务 IP 访问该端口。

4.  **启动 Teslamate**: 在 `docker-compose.yml` 文件所在目录运行 `docker-compose up -d` 启动服务。

### 第二步：配置并运行后端服务

后端服务负责连接数据库并提供 API。

1.  **修改配置文件**: 打开后端项目的核心配置文件 `src/main/resources/application.yml`。
2.  **配置数据库连接**:
    ```yaml
    spring:
      datasource:
        url: jdbc:postgresql://127.0.0.1:5432/teslamate
        username: teslamate
        password: <您在docker-compose.yml中设置的数据库密码>
    ```
3.  **配置系统参数 (`sys.config`)**:
    ```yaml
    sys:
      config:
        # 电池容量 (kWh)。示例：标续电池 60kWh，当前健康度 95%，则设置为 60 * 0.95 = 57.0
        batteryCapacity: 57.0
        # 每度电的电费 (元)。根据您的实际充电电价填写。
        costPerKwh: 0.9
        # 定义快充的功率阈值 (kW)。大于此值的充电被视为直流快充。
        fastChargePowerThresholdKw: 22.0
        # 标定百公里能耗 (kWh/100km)。用于估算能耗。
        # 示例：满电表显 417 公里，电池容量 57.0kWh，则设置为 57.0 / 417 * 100 = 13.67
        ratedConsumptionKwh: 13.67
        
        # --- 以下为小程序和 API 相关配置 ---
        # 小程序 appId (在微信公众平台获取)
        appId: 
        # 小程序 appSecret (在微信公众平台获取)
        appSecret: 
        # 您自己的小程序 openId (获取方法见下文)
        openId: 
        # 前后端接口签名密钥，自定义一个复杂的字符串，前后端保持一致
        signSecret: 1234qwerasdfzxcv1234awerasdfzxc
        # 百度地图逆地理编码服务的 AK (用于将经纬度转换为具体位置)
        # 申请地址: [https://lbsyun.baidu.com/apiconsole/key]
        baiduKey: 
        # 钉钉 Webhook 机器人 Token (用于推送行程结束等通知)
        webhookToken: 
    ```
4.  **获取 `openId`**:
    * 由于小程序为个人使用，通过 `openId` 进行身份验证。
    * **首先**，将 `openId` 字段留空，启动后端服务。
    * **然后**，打开微信小程序，触发登录请求（通常是打开小程序时自动触发）。
    * **最后**，查看后端服务的控制台日志，您会看到类似 `获取到openId: [一长串字符]` 的输出。
    * 将这串字符复制并填写到 `application.yml` 的 `openId` 字段中，然后重启后端服务。

5.  **运行后端**: 使用 Maven 运行 `mvn spring-boot:run` 或打包成 JAR 文件 `java -jar your-app.jar` 在服务器上运行。

### 第三步：配置并发布小程序

1.  **获取代码**: 小程序的前端文件位于项目根目录下的 `wechat` 目录。
2.  **使用微信开发者工具**: 打开微信开发者工具，选择“导入项目”，指向 `wechat` 目录。
3.  **修改 API 地址**:
    * 打开 `wechat/app.js` 文件。
    * 找到 `API_BASE_URL` 常量。
    * 将其值修改为您后端服务的公网访问地址（必须是 `https://` 域名）。
    ```javascript
    // app.js
    
    // const API_BASE_URL = 'http://localhost:8080'; // 开发时使用
    const API_BASE_URL = '[https://your.domain.com](https://your.domain.com)'; // !!! 修改为您的实际后端域名 !!!
    ```
4.  **预览和发布**: 在微信开发者工具中进行预览，确认所有功能正常后，即可上传发布。

## 注意事项

* **域名和 HTTPS**: 微信小程序要求后端 API 必须使用 `https://` 协议。您需要为您的后端服务配置域名和 SSL 证书。
* **安全性**: 请务必保管好您的数据库密码、`appSecret`、`signSecret` 等敏感信息，不要泄露到公共代码库中。
* **个人使用**: 当前的 `openId` 认证机制决定了该小程序仅供配置的 `openId` 持有者使用。
