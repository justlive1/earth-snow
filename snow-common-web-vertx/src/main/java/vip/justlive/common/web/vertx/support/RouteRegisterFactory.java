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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import com.google.common.collect.ImmutableList;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import vip.justlive.common.base.constant.BaseConstants;
import vip.justlive.common.base.convert.support.DefaultConverterService;
import vip.justlive.common.base.exception.Exceptions;
import vip.justlive.common.base.ioc.Ioc;
import vip.justlive.common.web.vertx.annotation.VertxRoute;
import vip.justlive.common.web.vertx.annotation.VertxRouteMapping;
import vip.justlive.common.web.vertx.exception.ErrorCodes;

/**
 * route注册工厂，用于解析Route相关注解并初始化Route
 * 
 * @author wubo
 * @see VertxRoute
 * @see VertxRouteMapping
 *
 */
@Slf4j
public class RouteRegisterFactory {

  private Map<String, RouteWrap> routeWraps;

  private Router router;

  private ParamResolverComposite paramResolver;

  public RouteRegisterFactory(Router router) {
    this.router = router;
    this.routeWraps = new HashMap<>(32);

    DefaultConverterService converterService = DefaultConverterService.sharedConverterService();

    this.paramResolver = new ParamResolverComposite(ImmutableList.<MethodParamResolver>builder()
        .add(new RequestParamResolver().converterService(converterService))
        .add(new HeaderParamResolver().converterService(converterService))
        .add(new PathParamResolver().converterService(converterService))
        .add(new RequestBodyResolver()).build());
  }

  /**
   * 执行route的解析和注册
   * 
   * @param basePackage 扫描包路径
   */
  public void execute(String... basePackage) {

    Reflections rel;
    if (basePackage != null && basePackage.length > 0) {
      rel = new Reflections("vip.justlive.common.web.vertx", basePackage);
    } else {
      rel = new Reflections();
    }
    Set<Class<?>> classes = rel.getTypesAnnotatedWith(VertxRoute.class);

    for (Class<?> clazz : classes) {
      if (clazz.isAnnotationPresent(VertxRoute.class)) {
        parseVertxRoute(clazz);
      }
    }

    register();

  }

  /**
   * 解析{@code VertxRoute}注解的route类
   * 
   * @param clazz 类
   */
  private void parseVertxRoute(Class<?> clazz) {

    VertxRoute route = clazz.getAnnotation(VertxRoute.class);

    Object bean = Ioc.instanceBean(clazz);
    if (bean == null) {
      throw new IllegalStateException(String.format("[%s]Ioc实例化失败", clazz));
    }
    String root = transferUri(route.value());
    Method[] methods = clazz.getMethods();

    for (Method method : methods) {
      if (method.isAnnotationPresent(VertxRouteMapping.class)) {
        parseVertxRouteMapping(root, method, bean);
      }
    }

  }

  /**
   * 解析{@code VertxRoute}注解类下注解了{@code VertxRouteMapping}的方法
   * 
   * @param root 根路径
   * @param method 方法
   * @param bean 实例
   */
  private void parseVertxRouteMapping(String root, Method method, Object bean) {

    VertxRouteMapping routeMapping = method.getAnnotation(VertxRouteMapping.class);
    String[] paths = routeMapping.value();

    String baseUrl = "";

    if (root.length() != 0) {
      baseUrl = BaseConstants.ROOT_PATH.concat(root);
    }

    for (String path : paths) {
      String url = "";
      String p = transferUri(path);
      if (p.length() != 0) {
        url = baseUrl.concat(BaseConstants.ROOT_PATH).concat(p);
      }

      if (routeWraps.containsKey(url)) {
        throw Exceptions.fail(ErrorCodes.URL_HAS_BOUND);
      }

      Parameter[] parameters = method.getParameters();
      ParamWrap[] paramWraps = parseVertxRouterParamters(parameters);

      RouteWrap wrap = new RouteWrap(url, method, bean, paramWraps, routeMapping);
      routeWraps.put(url, wrap);
    }

  }

  /**
   * 解析方法上使用了注解的参数
   * 
   * @param parameters 参数
   * @return 参数包装
   */
  private ParamWrap[] parseVertxRouterParamters(Parameter[] parameters) {

    ParamWrap[] paramWraps = new ParamWrap[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      if (RoutingContext.class.equals(parameters[i].getType())) {
        paramWraps[i] = new ParamWrap("", true, i, RoutingContext.class);
      } else if (paramResolver.supported(parameters[i])) {
        paramWraps[i] = paramResolver.resolve(parameters[i]);
      }
    }
    return paramWraps;
  }

  /**
   * 讲Route注册到vertx中
   */
  private void register() {

    for (Map.Entry<String, RouteWrap> entry : routeWraps.entrySet()) {
      RouteWrap wrap = entry.getValue();
      VertxRouteMapping routeMapping = wrap.getRouteMapping();
      Route route = router.route().path(entry.getKey());
      for (String consume : routeMapping.consumes()) {
        route.consumes(consume);
      }
      for (String produce : routeMapping.produces()) {
        route.produces(produce);
      }
      for (HttpMethod method : routeMapping.method()) {
        route.method(method);
      }
      Handler<RoutingContext> handler = ctx -> executeWithArgs(wrap, ctx);
      if (routeMapping.blocking()) {
        route.blockingHandler(handler);
      } else {
        route.handler(handler);
      }
      if (log.isDebugEnabled()) {
        log.debug("register url [{}]", entry.getKey());
      }
    }

  }

  /**
   * 绑定参数到Route上下文的方法
   * 
   * @param wrap 路由包装
   * @param ctx 路由上下文
   */
  private void executeWithArgs(RouteWrap wrap, RoutingContext ctx) {

    Object[] args = new Object[wrap.paramWraps.length];

    for (int i = 0; i < wrap.paramWraps.length; i++) {
      ParamWrap param = wrap.paramWraps[i];
      if (param != null) {
        if (RoutingContext.class.equals(param.getClazz())) {
          args[i] = ctx;
        } else {
          args[i] = paramResolver.render(param, ctx);
        }
      }
    }

    try {
      Object obj = wrap.method.invoke(wrap.bean, args);
      if (Void.TYPE != wrap.method.getReturnType()) {
        ctx.response().end(Json.encode(obj));
      }
    } catch (Exception e) {
      log.error("Router execute error", e);
      ctx.fail(500);
    }
  }

  /**
   * 处理uri，将首个/去除
   * 
   * @param uri 请求路径
   * @return 处理后的路径
   */
  private static String transferUri(String uri) {
    if (uri.startsWith(BaseConstants.ROOT_PATH)) {
      return uri.substring(1);
    }
    return uri;
  }

}
