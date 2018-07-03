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
package vip.justlive.common.spring.rabbit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;
import vip.justlive.common.spring.rabbit.config.RabbitConstant;

/**
 * rabbit queue
 * 
 * @author wubo
 *
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Rqueue {

  /**
   * 队列名称
   * 
   * @return 队列名称
   */
  public String queueName();

  /**
   * 交换机名称
   * 
   * @return 交换机名称
   */
  public String exchangeName() default RabbitConstant.DEFAULT_EXCHANGE_NAME;

  /**
   * 是否延迟队列
   * 
   * @return true是延迟队列
   */
  public boolean delay() default false;
}
