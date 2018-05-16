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
package vip.justlive.common.web.vertx.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参数解析包装
 * 
 * @author wubo
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParamWrap {

  /**
   * 参数名
   */
  private String value;

  /**
   * 是否必须
   */
  private boolean required;

  /**
   * resolve方法下标
   */
  private int index;

  /**
   * 参数类型
   */
  private Class<?> clazz;

}
