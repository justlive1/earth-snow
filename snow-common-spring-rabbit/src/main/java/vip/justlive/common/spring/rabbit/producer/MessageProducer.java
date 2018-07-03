/*
 * Copyright (C) 2018 justlive1
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package vip.justlive.common.spring.rabbit.producer;

import java.io.IOException;
import java.util.UUID;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vip.justlive.common.spring.rabbit.config.RabbitConstant;
import vip.justlive.common.spring.rabbit.core.HessianCodecFactory;
import vip.justlive.common.spring.rabbit.core.MessageBody;
import vip.justlive.common.spring.rabbit.core.RabbitBroker;

/**
 * 生产者
 * 
 * @author wubo
 *
 */
@Component
public class MessageProducer extends RabbitBroker {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  /**
   * 发送消息
   * 
   * @param queueName 队列名称
   * @param message 消息
   * @throws IOException 发送失败抛出
   */
  public void sendMessage(String queueName, Object message) throws IOException {
    String exchangeName = RabbitConstant.DEFAULT_EXCHANGE_NAME;
    this.sendMessage(exchangeName, queueName, message);
  }

  /**
   * 发送消息
   * 
   * @param exchangeName 交换机名称
   * @param queueName 队列名称
   * @param message 消息
   * @throws IOException 发送失败抛出
   */
  public void sendMessage(String exchangeName, String queueName, Object message)
      throws IOException {
    declareBindExchange(exchangeName, queueName, RabbitConstant.DEFAULT_DEAD_LETTER_EXCHANGE,
        queueName + RabbitConstant.DEFAULT_SEQUEUE_RETRY_SUFFIX);
    byte[] messageByte = HessianCodecFactory.serialize(message);
    MessageBody messageBody =
        new MessageBody(queueName, exchangeName, UUID.randomUUID().toString(), messageByte);
    rabbitTemplate.convertAndSend(exchangeName, queueName, messageBody);
  }

  /**
   * 发送延时消息
   * 
   * @param queueName 队列名称
   * @param message 消息
   * @param millisecond 毫秒值
   * @throws IOException 发送失败抛出
   */
  public void sendMessageWithDelay(String queueName, Object message, final long millisecond)
      throws IOException {
    String exchangeName = RabbitConstant.DEFAULT_EXCHANGE_NAME;
    this.sendMessageWithDelay(exchangeName, queueName, message, millisecond);
  }

  /**
   * 发送延时消息
   * 
   * @param exchangeName 交换机名称
   * @param queueName 队列名称
   * @param message 消息
   * @param millisecond 毫秒值
   * @throws IOException 发送失败抛出
   */
  public void sendMessageWithDelay(String exchangeName, String queueName, Object message,
      final long millisecond) throws IOException {
    declareBindExchange(RabbitConstant.DEFAULT_DELAY_EXCHANGE_NAME,
        queueName + RabbitConstant.DEFAULT_SEQUEUE_DELAY_SUFFIX, exchangeName, queueName);
    declareBindExchange(exchangeName, queueName, RabbitConstant.DEFAULT_DEAD_LETTER_EXCHANGE,
        queueName + RabbitConstant.DEFAULT_SEQUEUE_RETRY_SUFFIX);
    declareDeadLetterBindExchange(RabbitConstant.DEFAULT_DEAD_LETTER_EXCHANGE,
        queueName + RabbitConstant.DEFAULT_SEQUEUE_RETRY_SUFFIX,
        RabbitConstant.DEFAULT_DELAY_EXCHANGE_NAME,
        queueName + RabbitConstant.DEFAULT_SEQUEUE_DELAY_SUFFIX);
    byte[] messageByte = HessianCodecFactory.serialize(message);
    MessageBody messageBody =
        new MessageBody(queueName, exchangeName, UUID.randomUUID().toString(), messageByte);
    MessageConverter messageConverter = new SimpleMessageConverter();
    MessageProperties messageProperties = new MessageProperties();
    messageProperties.setExpiration(String.valueOf(millisecond));
    Object messageObject = messageConverter.toMessage(messageBody, messageProperties);
    rabbitTemplate.convertAndSend(RabbitConstant.DEFAULT_DELAY_EXCHANGE_NAME,
        queueName + RabbitConstant.DEFAULT_SEQUEUE_DELAY_SUFFIX, messageObject);
  }
}
