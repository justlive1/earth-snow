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

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import vip.justlive.common.base.domain.Response;

/**
 * ajax登录处理
 * 
 * @author wubo
 *
 */
@Slf4j
public class AjaxLoginHandlerImpl implements LoginHandler {

  /**
   * The default value of the form attribute which will contain the username
   */
  public static final String DEFAULT_U_PARAM = "username";

  /**
   * The default value of the form attribute which will contain the password
   */
  public static final String DEFAULT_P_PARAM = "password";

  protected final AuthProvider authProvider;

  protected String usernameParam;
  protected String passwordParam;
  protected boolean useJson = false;

  public AjaxLoginHandlerImpl(AuthProvider authProvider) {
    this(authProvider, DEFAULT_U_PARAM, DEFAULT_P_PARAM, false);
  }

  public AjaxLoginHandlerImpl(AuthProvider authProvider, String usernameParam, String passwordParam,
      boolean useJson) {
    this.authProvider = authProvider;
    this.usernameParam = usernameParam;
    this.passwordParam = passwordParam;
    this.useJson = useJson;
  }

  @Override
  public LoginHandler setUsernameParam(String usernameParam) {
    this.usernameParam = usernameParam;
    return this;
  }

  @Override
  public LoginHandler setPasswordParam(String passwordParam) {
    this.passwordParam = passwordParam;
    return this;
  }

  @Override
  public void handle(RoutingContext context) {

    HttpServerRequest req = context.request();
    if (req.method() != HttpMethod.POST) {
      // Must be a POST
      context.fail(405);
    } else {
      JsonObject authInfo = new JsonObject();
      boolean b = checkAndParseParam(authInfo, context);
      if (b) {
        auth(authInfo, context);
      } else {
        context.fail(400);
      }
    }
  }

  /**
   * 成功后处理
   * 
   * @param context RoutingContext
   */
  protected void success(RoutingContext context) {
    context.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .end(JsonObject.mapFrom(Response.success(context.user().principal())).toBuffer());
  }

  /**
   * 校验与解析入参
   * 
   * @param authInfo 认证信息
   * @param context RoutingContext
   * @return true是校验通过
   */
  protected boolean checkAndParseParam(JsonObject authInfo, RoutingContext context) {

    String username = null;
    String password = null;
    HttpServerRequest req = context.request();

    if (useJson) {
      JsonObject value = context.getBodyAsJson();
      username = value.getString(usernameParam);
      password = value.getString(passwordParam);
    } else {
      MultiMap params = req.params();
      username = params.get(usernameParam);
      password = params.get(passwordParam);
      params = req.formAttributes();
      if (username == null) {
        username = params.get(usernameParam);
      }
      if (password == null) {
        password = params.get(passwordParam);
      }
    }

    if (username == null || password == null) {
      log.warn(
          "No username or password provided in form - did you forget to include a BodyHandler?");
      return false;
    } else {
      authInfo.put(DEFAULT_U_PARAM, username).put(DEFAULT_P_PARAM, password);
      return true;
    }
  }

  /**
   * 认证
   * 
   * @param authInfo 认证信息
   * @param context RoutingContext
   */
  protected void auth(JsonObject authInfo, RoutingContext context) {
    authProvider.authenticate(authInfo, res -> {
      if (res.succeeded()) {
        User user = res.result();
        context.setUser(user);
        this.success(context);
      } else {
        // Failed login
        context.fail(403);
      }
    });
  }
}
