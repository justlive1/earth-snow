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
package vip.justlive.common.spring.rabbit.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import vip.justlive.common.spring.rabbit.config.RabbitConstant;
import vip.justlive.common.spring.rabbit.consumer.MessageProcess;

/**
 * Broker
 * 
 * @author wubo
 *
 */
public class RabbitBroker {

  @Autowired
  private AmqpAdmin amqpAdmin;

  private static Set<String> directBindCacheSet = new CopyOnWriteArraySet<>();
  private static ConcurrentHashMap<String, DirectExchange> directExchangeMap =
      new ConcurrentHashMap<>();
  private static ConcurrentHashMap<String, Queue> queueMap = new ConcurrentHashMap<>();

  private static ConcurrentMap<String, MessageProcess<?>> consumerMap = new ConcurrentHashMap<>();

  public static void putProcessIfAbsent(String key, MessageProcess<?> process) {
    consumerMap.putIfAbsent(key, process);
  }

  @SuppressWarnings("rawtypes")
  public static MessageProcess getProcess(String key) {
    return consumerMap.get(key);
  }

  protected void declareBindExchange(String exchangeName, String queueName, String toExchange,
      String routingKey) {
    if (directBindCacheSet.contains(exchangeName + RabbitConstant.SEPERATOR + queueName)) {
      return;
    }
    DirectExchange directExchange = null;
    if (!directExchangeMap.contains(exchangeName)) {
      directExchange = new DirectExchange(exchangeName, true, false, null);
      amqpAdmin.declareExchange(directExchange);
      directExchangeMap.putIfAbsent(exchangeName, directExchange);
    }
    Queue queue = null;
    if (!queueMap.contains(queueName)) {
      Map<String, Object> map = new HashMap<>(2, 1F);
      map.put("x-dead-letter-exchange", toExchange);
      map.put("x-dead-letter-routing-key", routingKey);
      queue = new Queue(queueName, true, false, false, map);
      amqpAdmin.declareQueue(queue);
      amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(directExchange).with(queueName));
      queueMap.putIfAbsent(queueName, queue);
      directBindCacheSet.add(exchangeName + RabbitConstant.SEPERATOR + queueName);
    }
  }

  protected void declareDeadLetterBindExchange(String exchangeName, String queueName,
      String toExchangeName, String routingKey) {
    if (directBindCacheSet.contains(exchangeName + RabbitConstant.SEPERATOR + queueName)) {
      return;
    }
    DirectExchange directExchange = null;
    if (!directExchangeMap.contains(exchangeName)) {
      directExchange = new DirectExchange(exchangeName, true, false, null);
      amqpAdmin.declareExchange(directExchange);
      directExchangeMap.putIfAbsent(exchangeName, directExchange);
    }
    Queue queue = null;
    if (!queueMap.contains(queueName)) {
      Map<String, Object> map = new HashMap<>(2, 1F);
      map.put("x-dead-letter-exchange", toExchangeName);
      map.put("x-dead-letter-routing-key", routingKey);
      map.put("x-message-ttl", 10000);
      queue = new Queue(queueName, true, false, false, map);
      amqpAdmin.declareQueue(queue);
      queueMap.putIfAbsent(queueName, queue);
    }
    String bindRelation = exchangeName + RabbitConstant.SEPERATOR + queueName;
    if (directBindCacheSet.contains(bindRelation)) {
      return;
    }
    amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(directExchange).with(queueName));
    directBindCacheSet.add(bindRelation);
  }
}
