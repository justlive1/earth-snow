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
package vip.justlive.common.web.vertx;

import java.util.Set;
import com.google.common.collect.ImmutableSet;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import vip.justlive.common.base.constant.BaseConstants;

/**
 * 基础web单元
 * 
 * @author wubo
 *
 */
public abstract class BaseWebVerticle extends AbstractVerticle {

  /**
   * 基础路由（LocalSessionStore）
   *
   * @param router router
   */
  protected void baseRoute(Router router) {
    baseRoute(router, LocalSessionStore.create(vertx));
  }

  /**
   * 基础路由
   *
   * @param router router
   * @param sessionStore 会话存储
   */
  protected void baseRoute(Router router, SessionStore sessionStore) {
    Set<HttpMethod> methods = ImmutableSet.<HttpMethod>builder().add(HttpMethod.GET)
        .add(HttpMethod.POST).add(HttpMethod.OPTIONS).add(HttpMethod.PUT).add(HttpMethod.DELETE)
        .add(HttpMethod.HEAD).build();

    Set<String> headers = ImmutableSet.of(HttpHeaderNames.ACCEPT.toString(),
        HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS.toString(),
        HttpHeaderNames.AUTHORIZATION.toString());

    router.route().handler(
        CorsHandler.create(BaseConstants.ANY).allowedMethods(methods).allowedHeaders(headers));
    router.route().handler(CookieHandler.create());
    router.route().handler(BodyHandler.create());
    router.route().handler(TimeoutHandler.create());
    router.route().handler(SessionHandler.create(sessionStore));
    router.exceptionHandler(this::log);
  }

  /**
   * 安全认证路由
   * 
   * @param router router
   * @param authProvider 认证提供
   * @param authHandler 认证处理
   * @param authUrl 认证路径统配
   */
  protected void authRoute(Router router, AuthProvider authProvider, AuthHandler authHandler,
      String authUrl) {
    if (authProvider != null && authHandler != null) {
      router.route().handler(UserSessionHandler.create(authProvider));
      router.route(authUrl).handler(authHandler);
    }
  }

  /**
   * 记录错误日志
   * 
   * @param e
   */
  protected void log(Throwable e) {
    LoggerFactory.getLogger(getClass()).error("something is wrong:", e);
  }
}
