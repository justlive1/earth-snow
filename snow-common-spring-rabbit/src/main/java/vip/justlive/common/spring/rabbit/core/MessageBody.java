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

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 消息实体
 * 
 * @author wubo
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageBody implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 队列名称
   */
  private String queueName;

  /**
   * 交换机名称
   */
  private String exchangeName;

  /**
   * key
   */
  private String deliverKey;

  /**
   * 消息
   */
  private byte[] messageData;

  @Override
  public String toString() {
    return String.format("{queueName=[%s], exchangeName=[%s], deliverKey=[%s]}", queueName,
        exchangeName, deliverKey);
  }
}
