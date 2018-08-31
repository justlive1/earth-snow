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
import io.vertx.ext.sql.UpdateResult;
import vip.justlive.common.base.datasource.ModelHelper;
import vip.justlive.common.base.datasource.TableInfo;

/**
 * Repository
 * 
 * @author wubo
 *
 * @param <T> 泛型
 */
public class Repository<T> {

  protected Class<T> clazz;

  protected final TableInfo tableInfo;

  @SuppressWarnings("unchecked")
  public Repository() {
    Type type = getClass().getGenericSuperclass();
    if (type instanceof ParameterizedType) {
      ParameterizedType pType = (ParameterizedType) type;
      Type pClazz = pType.getActualTypeArguments()[0];
      if (pClazz instanceof Class) {
        this.clazz = (Class<T>) pClazz;
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

  /**
   * 根据id获取实体
   * 
   * @param id id
   * @return JdbcPromise
   */
  public ModelPromise<T> findById(Serializable id) {
    RepositoryHelper.check(tableInfo);

    TableInfo.ColumnInfo primaryKey = tableInfo.getPrimaryKey();
    if (primaryKey == null) {
      throw new IllegalArgumentException("@Id 缺失");
    }
    String sql = String.format(RepositoryHelper.SQL_TEMPLATE_SELECT_BY_ID,
        tableInfo.getBaseColumnList(), tableInfo.getTableName(), primaryKey.getColumnName());
    ModelPromise<T> promise = new ModelPromise<>(this);
    jdbcClient().querySingleWithParams(sql, new JsonArray().add(id), promise);
    return promise;
  }

  /**
   * 根据model查询
   * 
   * @param model 实体
   * @return ModelsPromise
   */
  public ModelsPromise<T> findByModel(T model) {
    RepositoryHelper.check(tableInfo, model);

    ModelsPromise<T> promise = new ModelsPromise<>(this);
    JsonArray jsonArray = new JsonArray();
    String sql = RepositoryHelper.parseSelect(model, jsonArray, tableInfo);
    jdbcClient().queryWithParams(sql, jsonArray, promise);

    return promise;
  }

  /**
   * 持久化
   * 
   * @param model 实体
   * @return JdbcPromise
   */
  public JdbcPromise<UpdateResult> save(T model) {
    RepositoryHelper.check(tableInfo, model);

    JdbcPromise<UpdateResult> promise = new JdbcPromise<>();
    JsonArray jsonArray = new JsonArray();
    String sql = RepositoryHelper.parseInsert(model, jsonArray, tableInfo);
    jdbcClient().updateWithParams(sql, jsonArray, promise);

    return promise;
  }

  /**
   * 持久化
   * 
   * @param model 实体
   * @return JdbcPromise
   */
  public JdbcPromise<UpdateResult> update(T model) {
    RepositoryHelper.check(tableInfo, model);

    JdbcPromise<UpdateResult> promise = new JdbcPromise<>();
    JsonArray jsonArray = new JsonArray();
    String sql = RepositoryHelper.parseUpdate(model, jsonArray, tableInfo);
    jdbcClient().updateWithParams(sql, jsonArray, promise);

    return promise;
  }

  /**
   * 根据id删除记录
   * 
   * @param id id
   * @return promise
   */
  public JdbcPromise<UpdateResult> deleteById(Serializable id) {
    RepositoryHelper.check(tableInfo);

    TableInfo.ColumnInfo primaryKey = tableInfo.getPrimaryKey();
    if (primaryKey == null) {
      throw new IllegalArgumentException("@Id 缺失");
    }

    JdbcPromise<UpdateResult> promise = new JdbcPromise<>();
    String sql = String.format(RepositoryHelper.SQL_TEMPLATE_DELETE_BY_ID, tableInfo.getTableName(),
        tableInfo.getPrimaryKey().getColumnName());
    jdbcClient().updateWithParams(sql, new JsonArray().add(id), promise);
    return promise;
  }

}
