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
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import vip.justlive.common.web.vertx.annotation.VertxRequestBody;

/**
 * http body 解析器
 * 
 * @author wubo
 *
 */
public class RequestBodyResolver extends AbstractConverterParamResolver {

  @Override
  public boolean supported(Parameter parameter) {
    return parameter.isAnnotationPresent(VertxRequestBody.class);
  }

  @Override
  public ParamWrap resolve(Parameter parameter) {
    VertxRequestBody annotation = parameter.getAnnotation(VertxRequestBody.class);
    return new ParamWrap(null, annotation.required(), 0, parameter.getType());
  }

  @Override
  public Object render(ParamWrap wrap, RoutingContext ctx) {
    JsonObject value = ctx.getBodyAsJson();
    this.checkRequire(wrap, value);
    if (value != null) {
      return value.mapTo(wrap.getClazz());
    }
    return value;
  }

}
