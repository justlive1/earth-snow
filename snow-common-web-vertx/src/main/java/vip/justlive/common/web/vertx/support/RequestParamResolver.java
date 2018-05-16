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
import vip.justlive.common.web.vertx.annotation.VertxRequestParam;

/**
 * 请求参数解析器
 * 
 * @author wubo
 *
 */
public class RequestParamResolver extends AbastractConverterParamResolver {

  @Override
  public boolean supported(Parameter parameter) {
    return parameter.isAnnotationPresent(VertxRequestParam.class);
  }

  @Override
  public ParamWrap resolve(Parameter parameter) {
    VertxRequestParam annotation = parameter.getAnnotation(VertxRequestParam.class);
    return new ParamWrap(annotation.value(), annotation.required(), 0, parameter.getType());
  }

  @Override
  public Object render(ParamWrap wrap, RoutingContext ctx) {
    return converter(ctx.request().getParam(wrap.getValue()), wrap.getClazz());
  }

}
