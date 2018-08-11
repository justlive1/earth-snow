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
package vip.justlive.common.web.vertx.auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.AuthHandlerImpl;
import io.vertx.ext.web.handler.impl.HttpStatusException;

/**
 * token 认证
 * 
 * @author wubo
 *
 */
public class TokenAuthHandlerImpl extends AuthHandlerImpl implements TokenAuthHandler {

  public static final String DEFAULT_TOKEN_PARAM = "_token";
  public static final HttpStatusException UNAUTHORIZED = new HttpStatusException(401);

  private String tokenParam = DEFAULT_TOKEN_PARAM;

  public TokenAuthHandlerImpl(AuthProvider authProvider) {
    super(authProvider);
  }

  @Override
  public TokenAuthHandlerImpl setTokenParam(String tokenParam) {
    this.tokenParam = tokenParam;
    return this;
  }

  @Override
  public void parseCredentials(RoutingContext ctx, Handler<AsyncResult<JsonObject>> handler) {

    String token = ctx.request().getParam(tokenParam);
    if (token == null) {
      token = ctx.request().getFormAttribute(tokenParam);
    }
    if (token == null) {
      token = ctx.request().getHeader(tokenParam);
    }
    if (token == null) {
      handler.handle(Future.failedFuture(UNAUTHORIZED));
    } else {
      handler.handle(Future.succeededFuture(new JsonObject().put(DEFAULT_TOKEN_PARAM, token)));
    }
  }

}
