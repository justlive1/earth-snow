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
import java.util.List;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;

/**
 * 组合参数解析器
 * 
 * @author wubo
 *
 */
@AllArgsConstructor
public class ParamResolverComposite implements MethodParamResolver {

  private List<MethodParamResolver> resolvers;

  @Override
  public boolean supported(Parameter parameter) {
    for (MethodParamResolver resolver : resolvers) {
      if (resolver.supported(parameter)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ParamWrap resolve(Parameter parameter) {
    for (int i = 0, len = resolvers.size(); i < len; i++) {
      if (resolvers.get(i).supported(parameter)) {
        ParamWrap wrap = resolvers.get(i).resolve(parameter);
        wrap.setIndex(i);
        return wrap;
      }
    }
    return null;
  }

  @Override
  public Object render(ParamWrap wrap, RoutingContext ctx) {
    return resolvers.get(wrap.getIndex()).render(wrap, ctx);
  }

}
