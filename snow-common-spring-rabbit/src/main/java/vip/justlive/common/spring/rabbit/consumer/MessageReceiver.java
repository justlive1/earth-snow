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
package vip.justlive.common.spring.rabbit.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import vip.justlive.common.spring.rabbit.config.RabbitConstant;
import vip.justlive.common.spring.rabbit.core.HessianCodecFactory;
import vip.justlive.common.spring.rabbit.core.MessageBody;
import vip.justlive.common.spring.rabbit.core.RabbitBroker;

/**
 * 消息接收方
 * 
 * @author wubo
 *
 */
@Slf4j
@Component
public class MessageReceiver extends ApplicationObjectSupport
    implements ChannelAwareMessageListener {

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public void onMessage(Message message, Channel channel) throws Exception {
    MessageBody messageBody = null;
    try {
      MessageConverter messageConverter = new SimpleMessageConverter();
      Object messageObject = messageConverter.fromMessage(message);
      if (!(messageObject instanceof MessageBody)) {
        log.warn("receive message not instanceof MessageBody, message={}", messageObject);
        return;
      }
      messageBody = (MessageBody) messageObject;
      if (log.isDebugEnabled()) {
        log.debug("receive {}", messageBody);
      }
      StringBuilder queueBuild = new StringBuilder();
      queueBuild.append(messageBody.getExchangeName());
      queueBuild.append(RabbitConstant.SEPERATOR);
      queueBuild.append(messageBody.getQueueName());

      Object obj = HessianCodecFactory.deSerialize(messageBody.getMessageData());

      MessageProcess process = RabbitBroker.getProcess(queueBuild.toString());
      if (process == null) {
        log.error("receive bean is null! queueBuild={} message={}", queueBuild, messageBody);
        return;
      }
      process.process(obj);
      channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    } catch (Exception ex) {
      log.error("receive message body:{} error:", messageBody, ex);
      channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
    }
  }
}
