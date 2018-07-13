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

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.ext.web.RoutingContext;
import vip.justlive.common.base.domain.Response;

/**
 * jwt登录认证
 * 
 * @author wubo
 *
 */
public class JWTLoginHandlerImpl extends AjaxLoginHandlerImpl {

  private String algorithm;

  private final JWTAuth jwtAuth;

  public JWTLoginHandlerImpl(JWTAuth jwtAuth, AuthProvider authProvider) {
    super(authProvider);
    this.jwtAuth = jwtAuth;
  }

  public JWTLoginHandlerImpl(JWTAuth jwtAuth, AuthProvider authProvider, String usernameParam,
      String passwordParam, boolean useJson) {
    super(authProvider, usernameParam, passwordParam, useJson);
    this.jwtAuth = jwtAuth;
  }

  /**
   * 设置算法
   * 
   * @param algorithm
   * @return
   */
  public JWTLoginHandlerImpl setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
    return this;
  }

  @Override
  protected void success(RoutingContext context) {

    JWTOptions jwtOptions = new JWTOptions();
    if (algorithm != null) {
      jwtOptions.setAlgorithm(algorithm);
    }

    context.response()
        .end(JsonObject
            .mapFrom(
                Response.success(jwtAuth.generateToken(context.user().principal(), jwtOptions)))
            .toBuffer());
  }

}
