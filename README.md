# Transaction Management Application

这是提交面试测试项目

[相关要求](./_requirements.md)

## external libraries

|依赖库|用途|
|---|---|
|org.mockito.mockito-core|controller 接口单元测试|

## how to run

```bash
# 运行
mvn spring-boot:run

# 或者

mvn package && java -jar target/transaction-0.0.1-SNAPSHOT.jar 

# 测试
mvn test
```

### docker

```bash
bash build_docker.sh ${image_name} ${image_version}

# macos 需要加上-e JAVA_TOOL_OPTIONS="-XX:UseSVE=0"，openjdk 的基础镜像有问题
docker run -p ${your_port}:8080 ${image_name}:${image_port}
```

## page functionalities

访问根路径'/'，提供一个简单的 html 页面展示接口功能

## 接口文档

### 创建transaction

- path: `/api/transaction`
- method: POST
- request content type: application-json
- request body:

|key|type|is_required|desc|
|---|---|---|---|
|amount|BigDecimal|是||
|accountId|String|是||
|userId|String|是||
|type|String|是||
|status|String|是||

- response:
  - 成功：返回码 201，返回体是一个 json 对象，内容是创建后的 transaction 信息
  - 失败：返回码非 201，返回体 error 字段值表示错误原因

### 查询全部 transactions

- path: `/api/transaction`
- method: GET
- response：
  - 成功：返回码 200，返回一个 json 列表
  - 失败：非 200，error 字段值表示错误原因

### 查询单个transaction

- path: `/api/transaction/${id}`
- method: GET
- request param: 路径参数 id
- response:
  - 成功：返回码 200，返回一个 json 对象
  - 失败：非 200，error 字段值表示错误原因； 其中：id 对应 transaction 不存在返回 404

### 删除单个transaction

- path: `/api/transaction/${id}`
- method: DELETE
- request param: 路径参数 id
- response:
  - 成功：返回码 204
  - 失败：非 200，error 字段值表示错误原因； 其中：id 对应 transaction 不存在返回 404

### 完整更新单个transaction

- path: `/api/transaction`
- method: PUT
- request content type: application-json
- request body:

|key|type|is_required|desc|
|---|---|---|---|
|amount|BigDecimal|是||
|accountId|String|是||
|userId|String|是||
|type|String|是||
|status|String|是||

- response:
  - 成功：返回码 200，返回体是一个 json 对象，内容是更新后的 transaction 信息
  - 失败：返回码非 200，返回体 error 字段值表示错误原因
