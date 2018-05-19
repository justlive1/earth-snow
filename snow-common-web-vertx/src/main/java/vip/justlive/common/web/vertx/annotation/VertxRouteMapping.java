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
package vip.justlive.common.web.vertx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import io.vertx.core.http.HttpMethod;

/**
 * route的请求映射
 * 
 * @author wubo
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VertxRouteMapping {
  /**
   * 地址映射 (e.g. "/myPath.do").
   * 
   * @return 请求地址
   */
  String[] value() default {};

  /**
   * HTTP请求类型 :GET, POST, HEAD, OPTIONS, PUT, PATCH, DELETE, TRACE.
   * 
   * @return 请求类型
   */
  HttpMethod[] method() default {};

  /**
   * 指定处理请求的提交内容类型(Content-Type) *
   * 
   * <pre class="code">
   * consumes = "text/plain"
   * consumes = {"text/plain", "application/*"}
   * </pre>
   * 
   * @return Content-Type
   */
  String[] consumes() default {};

  /**
   * 指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回
   * 
   * 
   * <pre class="code">
   * produces = "text/plain"
   * produces = {"text/plain", "application/*"}
   * produces = "application/json; charset=UTF-8"
   * </pre>
   * 
   * @return Accept
   */
  String[] produces() default {};

  /**
   * 是否使用阻塞方式
   * 
   * @return true为阻塞方式
   */
  boolean blocking() default false;
}
