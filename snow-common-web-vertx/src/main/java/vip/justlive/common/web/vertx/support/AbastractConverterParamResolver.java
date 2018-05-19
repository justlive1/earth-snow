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

import vip.justlive.common.base.convert.ConverterService;
import vip.justlive.common.base.exception.Exceptions;
import vip.justlive.common.web.vertx.exception.ErrorCodes;

/**
 * 参数类型转换解析器
 * 
 * @author wubo
 *
 */
public abstract class AbastractConverterParamResolver implements MethodParamResolver {

  protected ConverterService converterService;

  public AbastractConverterParamResolver converterService(ConverterService converterService) {
    this.converterService = converterService;
    return this;
  }

  @SuppressWarnings("unchecked")
  protected <T> T converter(String source, Class<T> targetType) {
    if (source == null || targetType == String.class) {
      return (T) source;
    }
    if (converterService.canConverter(String.class, targetType)) {
      return converterService.convert(source, targetType);
    }
    throw Exceptions.fail(ErrorCodes.TYPE_CANNOT_CONVERTER, source, targetType);
  }

  protected void checkRequire(ParamWrap wrap, Object value) {
    if (wrap.isRequired() && value == null) {
      throw Exceptions.fail(ErrorCodes.PARAM_CANNOT_NULL);
    }
  }
}
