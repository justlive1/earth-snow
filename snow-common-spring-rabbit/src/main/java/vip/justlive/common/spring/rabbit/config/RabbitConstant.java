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

/**
 * 常量
 * 
 * @author wubo
 *
 */
public class RabbitConstant {

  RabbitConstant() {}

  public static final String SEPERATOR = "|";

  public static final String DEFAULT_EXCHANGE_NAME = "default-exchange";
  public static final String DEFAULT_DELAY_EXCHANGE_NAME = "default-delay-exchange";
  public static final String DEFAULT_DELAY_NOMAL_EXCHANGE_NAME = "default-delay-nomal-exchange";

  public static final String DEFAULT_DEAD_LETTER_EXCHANGE = "default-dead-letter-exchange";
  public static final String DEFAULT_SEQUEUE_RETRY_SUFFIX = "_retry";
  public static final String DEFAULT_SEQUEUE_DELAY_SUFFIX = "_delay";
}
