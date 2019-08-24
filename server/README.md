# API 说明

Dandelion 后端 API 说明，后端只做数据中转。同一用户可以重复提交通讯录，但是以最新的为主。
并没有重置密码的功能，如果忘记密码就重新注册一个账号吧 :)

安卓在开发时需要确保 API 地址可动态设置，即可做到服务器随用随开，不用就停。

## 测试用服务器

https://dandelion.xlui.app

## 1. 注册

`/register`

接收 JSON 数据：

```json
{
  "username": "someUsername",
  "password": "somePassword"
}
```

返回自定义的 Response：

```json
{
  "code": 0,
  "data": "成功注册！",
  "error": null
}
```

或者：

```json
{
  "code": 10000,
  "data": null,
  "error": "请求数据缺少用户名或密码！"
}
```

只有在 `code` 为 0 时才是请求成功，此时 `data` 中的数据有效，`error` 无效。
`code` 不为 0 代表请求失败，此时 `code` 为错误码，`error` 为错误信息，`data` 无效。

## 2. 登录

`/login`

接收 JSON 数据：

```json
{
  "username": "1",
  "password": "dev"
}
```

返回 `access_token`：

```json
{
  "access_token": "a long token"
}
```

由于登录是托管给 Flask-JWT 的，没办法自定义返回数据做到统一。

## 3. 上传通讯录

`/push`，此 API 需要 `access_token`。

请求头需要添加认证：

```
Authorization: JWT access_token
```

请求体直接上传 JSON 数据即可，服务端只做保存。

## 4. 下载通讯录

`/pull`，此 API 需要 `access_token`。

请求头需要添加认证：

```
Authorization: JWT access_token
```

会返回 Token 对应用户保存的通讯录，即在上一步上传的 JSON。
