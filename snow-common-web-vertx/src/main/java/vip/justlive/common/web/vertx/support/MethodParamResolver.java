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

import java.lang.reflect.Parameter;
import io.vertx.ext.web.RoutingContext;

/**
 * 方法参数处理器
 * 
 * @author wubo
 *
 */
public interface MethodParamResolver {

  /**
   * 当前方法参数是否支持
   * 
   * @param parameter 参数
   * @return true为支持
   */
  boolean supported(Parameter parameter);

  /**
   * 解析当前方法参数
   * 
   * @param parameter 参数
   * @return 参数解析结果
   */
  ParamWrap resolve(Parameter parameter);

  /**
   * 渲染获取当前参数值
   * 
   * @param wrap 参数包装
   * @param ctx 路由上下文
   * @return 渲染对象
   */
  Object render(ParamWrap wrap, RoutingContext ctx);
}
