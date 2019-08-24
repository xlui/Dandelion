# Dandelion

通讯录迁移工具，用于更换手机时快速迁移通讯录。

## v0.1 功能需求

1. 读取旧机的联系人列表，存入服务器
2. 新机从服务器拉取联系人列表，写入本地通讯录
3. 如果本地通讯录已经有同名联系人，则判断要写入的手机号是否已经存在，如果存在则跳过，如果不存在则在联系人下新增手机号
4. 客户端需要支持动态更换服务器的 URL
5. 服务器实现尽量简洁并支持 Docker 启动，做到随用随开，不用就停。

## Docker

服务端现已支持 Docker 启动：

```bash
docker pull xlui/dandelion
docker run --name dandelion -p 127.0.0.1:5000:5000 -d xlui/dandelion
```

[Docker: Dandelion](https://cloud.docker.com/u/xlui/repository/docker/xlui/dandelion)

# License

MIT.

