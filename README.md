# cloudarg

## 数据

路由数据：

```json

{
  "lang": "zh-CN",
  "service": "cloudarg-service",
  "apiVersion": "0.0.1",
  "timeStamp": 1638089866670,
  "success": true,
  "data": {
    "id": 1,
    "title": "列出路由",
    "project_id": 1,
    "version": "v1",
    "path": "/list-route",
    "full_path": "/cloudarg/v1/list-route",
    "authorized_roles": [
      "admin"
    ],
    "forbidden_roles": [
      "banned"
    ],
    "backends": [
      {
        "id": 1,
        "title": null,
        "env": "prod",
        "host": "localhost",
        "port": 7302,
        "schema": "http",
        "path": "/inner/role/list",
        "weight": 100,
        "header_pattern": null,
        "path_pattern": null,
        "query_pattern": null,
        "body_pattern": null
      },
      {
        "id": 2,
        "title": null,
        "env": "prod",
        "host": "localhost",
        "port": 8081,
        "schema": "http",
        "path": "/inner/role/list",
        "weight": 100,
        "header_pattern": null,
        "path_pattern": null,
        "query_pattern": null,
        "body_pattern": null
      },
      {
        "id": 3,
        "title": null,
        "env": "prod",
        "host": "localhost",
        "port": 8081,
        "schema": "http",
        "path": "/inner/role/list2",
        "weight": 0,
        "header_pattern": "/*./",
        "path_pattern": null,
        "query_pattern": null,
        "body_pattern": null
      }
    ]
  }
}
```

用户数据

```json

{
  "lang": "zh-CN",
  "service": "cloudarling",
  "apiVersion": "0.0.1",
  "timeStamp": 1638090994907,
  "success": true,
  "data": {
    "id": 1,
    "open_id": "5e58e016-bb37-4f40-899d-67bfcccd5caf",
    "nick": "啤酒邱",
    "mobile": "13000000000",
    "email": "chuchur@qq.com",
    "roles": [
      "admin"
    ]
  }
}

```
## 功能

### 权限验证

通过用户的 roles 和 route 的 roles 进行匹配。

### 灰度（访问权重）

可以为一个 route 指定多个 后端服务，通过 weight 来决定访问权重。

## 参数变化

为后端服务增加了 2 个入参

参数放在了在 header 中。

```
__cloudaring_user__: ewogICJpZCI6IDEsCi...==

__request_id__: 8cc4a0a2-11ef-4985-9435-111d70398b2a
```

在 cloudoll 的 auth 中间件获取用户和 request_id：

```js

const cloudoll = require("cloudoll");
module.exports = options => {
  const auth = async (ctx, next) => {
    const userHeader = ctx.headers["__cloudaring_user__"];
    if (userHeader) {
      try {
        ctx.user = JSON.parse(cloudoll.tools.base64Decode(userHeader));
      } catch (e) {
        ctx.logger.error(e);
      }
    }
    ctx.request_id = ctx.headers["__request_id__"];
    await next();
  }
  return auth;
}
```


## TODO

- 优化获取 route 和 user 的逻辑：增加缓存 redis
- 增加新功能：各种 pattern
- 大body传输可能会出错（为处理 512K 以上的传输）
- 支持 https 访问