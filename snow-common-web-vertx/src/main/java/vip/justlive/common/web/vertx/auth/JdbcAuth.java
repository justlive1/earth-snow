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

import java.util.function.Consumer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import lombok.Data;
import vip.justlive.common.base.crypto.Encoder;
import vip.justlive.common.base.crypto.Md5Encoder;

/**
 * jdbc认证提供 <br>
 * 支持Encoder
 * 
 * @author wubo
 *
 */
@Data
public class JdbcAuth implements AuthProvider {

  public static final String DEFAULT_AUTHENTICATE_QUERY =
      "SELECT PASSWORD FROM USER WHERE USERNAME = ?";

  private JDBCClient client;
  private String authenticateQuery = DEFAULT_AUTHENTICATE_QUERY;
  private String rolesQuery = JDBCAuth.DEFAULT_ROLES_QUERY;
  private String permissionsQuery = JDBCAuth.DEFAULT_PERMISSIONS_QUERY;
  private String rolePrefix = JDBCAuth.DEFAULT_ROLE_PREFIX;
  private Encoder encoder;

  public JdbcAuth(JDBCClient client) {
    this.client = client;
    encoder = new Md5Encoder();
  }

  /**
   * 加密密码
   * 
   * @param password 密码
   * @return 加密字符串
   */
  public String encode(String password) {
    return encoder.encode(password);
  }

  @Override
  public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {

    String username = authInfo.getString("username");
    if (username == null) {
      resultHandler
          .handle(Future.failedFuture("authInfo must contain username in 'username' field"));
      return;
    }
    String password = authInfo.getString("password");
    if (password == null) {
      resultHandler
          .handle(Future.failedFuture("authInfo must contain password in 'password' field"));
      return;
    }
    executeQuery(authenticateQuery, new JsonArray().add(username), resultHandler, rs -> {

      switch (rs.getNumRows()) {
        case 0: {
          // Unknown user/password
          resultHandler.handle(Future.failedFuture("Invalid username/password"));
          break;
        }
        case 1: {
          if (encoder.match(password, rs.getResults().get(0).getString(0))) {
            resultHandler.handle(Future.succeededFuture(new JdbcUser(username, this, rolePrefix)));
          } else {
            resultHandler.handle(Future.failedFuture("Invalid username/password"));
          }
          break;
        }
        default: {
          // More than one row returned!
          resultHandler.handle(Future.failedFuture("Failure in authentication"));
          break;
        }
      }
    });
  }


  <T> void executeQuery(String query, JsonArray params, Handler<AsyncResult<T>> resultHandler,
      Consumer<ResultSet> resultSetConsumer) {
    client.getConnection(res -> {
      if (res.succeeded()) {
        SQLConnection conn = res.result();
        conn.queryWithParams(query, params, queryRes -> {
          if (queryRes.succeeded()) {
            ResultSet rs = queryRes.result();
            resultSetConsumer.accept(rs);
          } else {
            resultHandler.handle(Future.failedFuture(queryRes.cause()));
          }
          conn.close(closeRes -> {
          });
        });
      } else {
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
  }

}
