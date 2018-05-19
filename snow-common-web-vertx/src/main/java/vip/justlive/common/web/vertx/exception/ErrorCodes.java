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
package vip.justlive.common.web.vertx.exception;

import vip.justlive.common.base.exception.ErrorCode;
import vip.justlive.common.base.exception.Exceptions;

/**
 * 错误编码
 * 
 * @author wubo
 *
 */
public class ErrorCodes {

  private ErrorCodes() {}

  /**
   * vertx-web模块名
   */
  public static final String MODULE = "VERTX-WEB";

  /**
   * url已绑定
   */
  public static final ErrorCode URL_HAS_BOUND =
      Exceptions.errorMessage(MODULE, "00000", "url[%s]重复绑定");

  /**
   * 类型不能转换
   */
  public static final ErrorCode TYPE_CANNOT_CONVERTER =
      Exceptions.errorMessage(MODULE, "00001", "[%]不能转换为[%s]类型");

  /**
   * 参数不能为空
   */
  public static final ErrorCode PARAM_CANNOT_NULL =
      Exceptions.errorMessage(MODULE, "00002", "参数不能为空");
}
