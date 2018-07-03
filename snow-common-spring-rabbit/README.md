## 生产者开发
消息的发送方只需要写以下代码，就可以实现消息的发送

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

MessageProducer需要通过Spring注入

sendMessage方法，第一个参数是交换机名称，第二个参数是队列的名称，第三个参数是消息实体

sendMessageWithDelay方法，第一个参数是交换机名称，第二个参数是队列的名称，第三个参数是消息实体，第四个参数是延迟的时间，单位毫秒

## 消费者开发

```
@Rqueue(queueName = "q.sms", exchangeName = "e.sms")
public class MessageProcessDemo implements MessageProcess<Vo> {

  @Override
  public void process(Vo message) {
    System.out.println("process message -> " + JSON.toJSON(message));
  }

}
```
delay=true 是延迟消息 。使用延迟消息 ，务必加上此注解。

- 实现MessageProcess接口，在process方法体内做消息的业务处理。
- 加上@Rqueue注解，如果程序要消费多个不同的队列，那就再写多个这样的类，实现MessageProcess。 


自动配置需要加上vip.justlive.common.spring.rabbit包的扫描
