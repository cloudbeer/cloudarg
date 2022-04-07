# rust gateway

## 连接器

### 账号系统和权限系统

首先访问网关的时候，需要提供 ticket， 有如下两种方式提供：

- 在 querystring 里提供，如： \_\_ticket=foobar
- 在 header 中提供，如：Authorization: bear boobar

同时需要提供一个获取用户信息和角色的 account_url，此 url 请配置在 config.yaml 里。

Cloudarg 会发起 GET 请求：${account_url}?ticket=foobar 来请求 Account 数据，此 Url 应该返回如下数据：

```
{
  "success": true,
  "data": {
    "id": 1,
    "open_id": "9e8d5e33-8b20-4544-b1a0-2ec1661bd556",
    "nick": "啤酒邱",
    "mobile": "",
    "email": "chuchur@qq.com",
    "roles": [
      "god",
      "dims-admin"
    ]
  }
}
```

## 内置 API

内置 API 在另一个端口。

### 清除用户缓存请执行：

POST /clear-cache

type: account
open_id: 9e8d5e33-8b20-4544-b1a0-2ec1661bd556

### 清除路由缓存请执行：

POST /clear-cache

type: route
path: /biz-url

### metrics 接口

GET /metrics

符合 Prometheus 规则
