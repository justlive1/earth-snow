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
package vip.justlive.common.spring.rabbit.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vip.justlive.common.spring.rabbit.annotation.Rqueue;
import vip.justlive.common.spring.rabbit.consumer.MessageProcess;
import vip.justlive.common.spring.rabbit.consumer.MessageReceiver;
import vip.justlive.common.spring.rabbit.core.RabbitBroker;

/**
 * rabbit配置
 * 
 * @author wubo
 *
 */
@Configuration
public class RabbitConfig extends RabbitBroker {

  @Bean
  public SimpleMessageListenerContainer initConstainer(ConnectionFactory connectionFactory,
      MessageListenerAdapter messageListenerAdapter, Map<String, MessageProcess<?>> processMap) {
    SimpleMessageListenerContainer container =
        new SimpleMessageListenerContainer(connectionFactory);
    container.setMessageListener(messageListenerAdapter);
    List<String> queueNames = new ArrayList<>();
    for (Map.Entry<String, MessageProcess<?>> entry : processMap.entrySet()) {
      MessageProcess<?> process = entry.getValue();
      if (process.getClass().isAnnotationPresent(Rqueue.class)) {
        Rqueue rqueue = process.getClass().getAnnotation(Rqueue.class);
        String queueName = rqueue.queueName();
        String exchangeName = rqueue.exchangeName();
        boolean delay = rqueue.delay();
        StringBuilder queueBuild = new StringBuilder();
        if (delay) {
          queueBuild.append(exchangeName);
          declareBindExchange(RabbitConstant.DEFAULT_DELAY_EXCHANGE_NAME,
              queueName + RabbitConstant.DEFAULT_SEQUEUE_DELAY_SUFFIX, exchangeName, queueName);
          declareBindExchange(exchangeName, queueName, RabbitConstant.DEFAULT_DEAD_LETTER_EXCHANGE,
              queueName + RabbitConstant.DEFAULT_SEQUEUE_RETRY_SUFFIX);
          declareDeadLetterBindExchange(RabbitConstant.DEFAULT_DEAD_LETTER_EXCHANGE,
              queueName + RabbitConstant.DEFAULT_SEQUEUE_RETRY_SUFFIX,
              RabbitConstant.DEFAULT_DELAY_EXCHANGE_NAME,
              queueName + RabbitConstant.DEFAULT_SEQUEUE_DELAY_SUFFIX);
        } else {
          queueBuild.append(exchangeName);
          declareBindExchange(exchangeName, queueName, RabbitConstant.DEFAULT_DEAD_LETTER_EXCHANGE,
              queueName + RabbitConstant.DEFAULT_SEQUEUE_RETRY_SUFFIX);
          declareDeadLetterBindExchange(RabbitConstant.DEFAULT_DEAD_LETTER_EXCHANGE,
              queueName + RabbitConstant.DEFAULT_SEQUEUE_RETRY_SUFFIX, exchangeName, queueName);

        }
        queueBuild.append(RabbitConstant.SEPERATOR);
        queueBuild.append(queueName);
        queueNames.add(queueName);
        putProcessIfAbsent(queueBuild.toString(), process);
      }
      container.setQueueNames(queueNames.toArray(new String[] {}));
      container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
      container.setPrefetchCount(1);
      container.setTxSize(1);
    }
    return container;
  }

  @Bean
  MessageListenerAdapter listenerAdapter(MessageReceiver receiver) {
    return new MessageListenerAdapter(receiver, new SimpleMessageConverter());
  }

}
