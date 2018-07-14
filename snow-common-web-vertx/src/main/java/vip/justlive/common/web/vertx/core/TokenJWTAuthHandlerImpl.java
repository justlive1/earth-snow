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
package vip.justlive.common.web.vertx.core;

import java.util.List;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import io.vertx.ext.web.handler.impl.JWTAuthHandlerImpl;

/**
 * 使用jwt的token认证处理实现
 * 
 * @author wubo
 *
 */
public class TokenJWTAuthHandlerImpl extends JWTAuthHandlerImpl implements TokenAuthHandler {

  public static final String DEFAULT_TOKEN_PARAM = "_token";

  private String tokenParam = DEFAULT_TOKEN_PARAM;

  private JsonObject options = new JsonObject();

  public TokenJWTAuthHandlerImpl(JWTAuth authProvider, String skip) {
    super(authProvider, skip);
  }

  @Override
  public TokenJWTAuthHandlerImpl setTokenParam(String tokenParam) {
    this.tokenParam = tokenParam;
    return this;
  }

  @Override
  public TokenJWTAuthHandlerImpl setAudience(List<String> audience) {
    options.put("audience", new JsonArray(audience));
    super.setAudience(audience);
    return this;
  }

  @Override
  public TokenJWTAuthHandlerImpl setIssuer(String issuer) {
    options.put("issuer", issuer);
    super.setIssuer(issuer);
    return this;
  }

  @Override
  public TokenJWTAuthHandlerImpl setIgnoreExpiration(boolean ignoreExpiration) {
    options.put("ignoreExpiration", ignoreExpiration);
    super.setIgnoreExpiration(ignoreExpiration);
    return this;
  }

  @Override
  public void handle(RoutingContext ctx) {
    String token = ctx.request().getParam(tokenParam);
    if (token == null) {
      token = ctx.request().getFormAttribute(tokenParam);
    }

    if (token != null) {
      this.handleToken(token, ctx);
    } else {
      super.handle(ctx);
    }
  }

  private void handleToken(String token, RoutingContext ctx) {
    this.authProvider.authenticate(new JsonObject().put("jwt", token).put("options", options),
        authN -> {
          if (authN.succeeded()) {
            User authenticated = authN.result();
            ctx.setUser(authenticated);
            Session session = ctx.session();
            if (session != null) {
              session.regenerateId();
            }
            authorize(authenticated, authZ -> {
              if (authZ.failed()) {
                processException(ctx, authZ.cause());
                return;
              }
              ctx.next();
            });
          } else {
            processException(ctx, new HttpStatusException(401));
          }
        });
  }
}
