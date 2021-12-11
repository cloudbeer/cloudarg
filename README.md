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

### 规则

- 如果 route 以 / 结尾，那么为模糊匹配，相应的 backend 应该也是 / 结尾，否则会加一个 /
- 模糊匹配的会从后往前匹配，匹配最长字段。
- 如果 route 没有 / 则为精确匹配。
- wrapper 封装情况下，如果原始结果为 json，则正常封装，其他类型返回 base64 结果。


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

## 缓存 redis 规则

### user

用户缓存 key 规则如下， value 是 User 的 Json 序列化

```
"t" + hashCode(ticket) + crc32(ticket) =  serialize(User)
```


### route

用户 path 缓存 key 规则如下， value 是 Route 的 Json 序列化

```
"p" + hashCode(path) + crc32(path) = serialize(Route)
```

同时，将使用到 Route 的 path 的 key 缓存

```
"r" + routeId = append(routeKey)
```

当 route 配置更新的时候，将同时删除相关 的 r 和 p 。 （后台管理实现，当前尚未实现。）




## TODO

- rate limit （系统级，route 级）
- 黑名单，白名单（系统级，route 级）
- 增加新功能：各种 pattern
- 大 body 传输可能会出错（未处理 512K 以上的传输）
- 统计 qps
- 内部服务 支持 https 访问（当前不计划支持）