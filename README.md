
# 基础服务


## 介绍

- snow-common-base J2SE基础
- snow-common-web J2EE基础
- snow-common-web-vertx Vertx Web扩展
- snow-common-spring-rabbit spring-rabbit封装
- 依赖方式

```
<dependency>
    <groupId>vip.justlive</groupId>
    <artifactId>snow-common-base</artifactId>
    <version>${snow.version}</version>
</dependency>

<dependency>
    <groupId>vip.justlive</groupId>
    <artifactId>snow-common-web</artifactId>
    <version>${snow.version}</version>
</dependency>

<dependency>
    <groupId>vip.justlive</groupId>
    <artifactId>snow-common-web-vertx</artifactId>
    <version>${snow.version}</version>
</dependency>

<dependency>
    <groupId>vip.justlive</groupId>
    <artifactId>snow-common-spring-rabbit</artifactId>
    <version>${snow.version}</version>
</dependency>

```


## 特性

* 基于properties配置进行项目定制
* 极简xml
* 自动装载 依赖jar配置properties无需额外配置

## 开发
	
	
	snow-common-base
		公共常量
		异常处理封装
		工具类
	snow-common-web
		web异常处理封装
		webmvc基本组件配置提供
		用户相关工具类
		公共对外rest接口提供
		
	snow-common-web-vertx
		扩展Route增加注解实现
		
	snow-common-spring-rabbit
		spring-rabbit的封装
		

## 使用

### Response返回

RestController返回使用vip.justlive.common.base.domain.Response进行包装

```
# 成功返回  成功编码 00000
Response.success(T t);
# 错误返回 默认错误码 99999
Response.error(String msg);
# 自定义错误编码
Response.error(String code, String msg);

```

### 异常处理

代码中需要throw异常时需要使用vip.justlive.common.base.exception.Exceptions进行包装，抛出CodedException

注意：
- 异常尽量不要try catch
- WebExceptionHandler统一处理了异常

```
# 构造ErrorCode
ErrorCode err = Exceptions.errorCode(String module, String code);
ErrorCode err = Exceptions.errorMessage(String module, String code, String message);

# 异常包装，抛出unchecked异常
throw Exceptions.wrap(Throwable e);
throw Exceptions.wrap(Throwable e, String code, String message);
throw Exceptions.wrap(Throwable e, ErrorCode errorCode, Object... arguments);

# 抛出业务逻辑异常，不含堆栈信息
throw Exceptions.fail(ErrorCode errCode, Object... params);
throw Exceptions.fail(String code, String message, Object... params);

# 故障型异常，包含堆栈信息
throw Exceptions.fault(ErrorCode errCode, Object... params);
throw Exceptions.fault(String code, String message, Object... params);
throw Exceptions.fault(Throwable e, ErrorCode errCode, Object... params);
throw Exceptions.fault(Throwable e, String code, String message, Object... params)

```

### rabbit

依赖jar

```
<dependency>
    <groupId>vip.justlive</groupId>
    <artifactId>snow-common-spring-rabbit</artifactId>
    <version>${snow.version}</version>
</dependency>
```

生产者开发
- 消息的发送方只需要写以下代码，就可以实现消息的发送
- MessageProducer需要通过Spring注入
- sendMessage方法，第一个参数是交换机名称，第二个参数是队列的名称，第三个参数是消息实体
- sendMessageWithDelay方法，第一个参数是交换机名称，第二个参数是队列的名称，第三个参数是消息实体，第四个参数是延迟的时间，单位毫秒

```
  @Autowired
  MessageProducer producer;

  @Test
  public void testRabbit() throws IOException {
    Vo message = new Vo();
    message.setCode("12313");
    message.setDate(new Date());
    producer.sendMessage("e.sms", "q.sms", message);
  }
```

消费者开发
- delay=true 是延迟消息 。使用延迟消息 ，务必加上此注解
- 实现MessageProcess接口，在process方法体内做消息的业务处理
- 加上@Rqueue注解，如果程序要消费多个不同的队列，那就再写多个这样的类，实现MessageProcess
- 自动配置需要加上vip.justlive.common.spring.rabbit包的扫描

```
@Rqueue(queueName = "q.sms", exchangeName = "e.sms")
public class MessageProcessDemo implements MessageProcess<Vo> {

  @Override
  public void process(Vo message) {
    System.out.println("process message -> " + JSON.toJSON(message));
  }
}
```

