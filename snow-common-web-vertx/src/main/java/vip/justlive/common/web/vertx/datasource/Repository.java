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
package vip.justlive.common.web.vertx.datasource;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * Repository
 * 
 * @author wubo
 *
 * @param <T> 泛型
 */
public class Repository<T> {

  private static final String SQL_TEMPLATE_SELECT_BY_ID = "select * from %s where id = ?";

  protected Class<?> clazz;

  private final TableInfo tableInfo;

  Repository() {
    Type type = getClass().getGenericSuperclass();
    if (type instanceof ParameterizedType) {
      ParameterizedType pType = (ParameterizedType) type;
      Type pClazz = pType.getActualTypeArguments()[0];
      if (pClazz instanceof Class) {
        this.clazz = (Class<?>) pClazz;
      }
    }
    if (clazz == null) {
      throw new IllegalArgumentException("获取泛型失败");
    }
    this.tableInfo = ModelHelper.getTableInfo(clazz);
  }


  /**
   * 获取jdbcClient
   *
   * @return jdbcClient
   */
  public JDBCClient jdbcClient() {
    return DataSourceFactory.sharedSingleJdbcClient(getClass());
  }

  public void findById(Serializable id) {
    if (tableInfo == null || tableInfo.primaryKey == null) {
      throw new IllegalArgumentException("当前实体没有主键");
    }

    String sql = String.format(SQL_TEMPLATE_SELECT_BY_ID, tableInfo.tableName);
    jdbcClient().querySingleWithParams(sql, new JsonArray().add(id), rs -> {
      if (rs.succeeded()) {
        JsonArray jsonArray = rs.result();
        System.out.println(jsonArray);
      }
      // TODO error
    });
  }

}
