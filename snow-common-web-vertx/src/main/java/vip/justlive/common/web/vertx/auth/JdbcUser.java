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

import java.nio.charset.StandardCharsets;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;

/**
 * jdbcUser
 * 
 * @author wubo
 *
 */
public class JdbcUser extends AbstractUser {

  private JdbcAuth authProvider;
  private String username;
  private JsonObject principal;

  private String rolePrefix;

  public JdbcUser() {}

  JdbcUser(String username, JdbcAuth authProvider, String rolePrefix) {
    this.username = username;
    this.authProvider = authProvider;
    this.rolePrefix = rolePrefix;
  }

  @Override
  public void doIsPermitted(String permissionOrRole, Handler<AsyncResult<Boolean>> resultHandler) {
    if (permissionOrRole != null && permissionOrRole.startsWith(rolePrefix)) {
      hasRoleOrPermission(permissionOrRole.substring(rolePrefix.length()),
          authProvider.getRolesQuery(), resultHandler);
    } else {
      hasRoleOrPermission(permissionOrRole, authProvider.getPermissionsQuery(), resultHandler);
    }
  }

  @Override
  public JsonObject principal() {
    if (principal == null) {
      principal = new JsonObject().put("username", username);
    }
    return principal;
  }

  @Override
  public void setAuthProvider(AuthProvider authProvider) {
    if (authProvider instanceof JdbcAuth) {
      this.authProvider = (JdbcAuth) authProvider;
    } else {
      throw new IllegalArgumentException("Not a JdbcAuth");
    }
  }

  @Override
  public void writeToBuffer(Buffer buff) {
    super.writeToBuffer(buff);
    byte[] bytes = username.getBytes(StandardCharsets.UTF_8);
    buff.appendInt(bytes.length);
    buff.appendBytes(bytes);

    bytes = rolePrefix.getBytes(StandardCharsets.UTF_8);
    buff.appendInt(bytes.length);
    buff.appendBytes(bytes);
  }

  @Override
  public int readFromBuffer(int pos, Buffer buffer) {
    pos = super.readFromBuffer(pos, buffer);
    int len = buffer.getInt(pos);
    pos += 4;
    byte[] bytes = buffer.getBytes(pos, pos + len);
    username = new String(bytes, StandardCharsets.UTF_8);
    pos += len;

    len = buffer.getInt(pos);
    pos += 4;
    bytes = buffer.getBytes(pos, pos + len);
    rolePrefix = new String(bytes, StandardCharsets.UTF_8);
    pos += len;

    return pos;
  }

  private void hasRoleOrPermission(String roleOrPermission, String query,
      Handler<AsyncResult<Boolean>> resultHandler) {
    authProvider.executeQuery(query, new JsonArray().add(username), resultHandler, rs -> {
      boolean has = false;
      for (JsonArray result : rs.getResults()) {
        String theRoleOrPermission = result.getString(0);
        if (roleOrPermission.equals(theRoleOrPermission)) {
          resultHandler.handle(Future.succeededFuture(true));
          has = true;
          break;
        }
      }
      if (!has) {
        resultHandler.handle(Future.succeededFuture(false));
      }
    });
  }

}
