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
import java.util.ArrayList;
import java.util.List;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
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

  public Repository() {
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

  /**
   * 根据id获取实体
   * 
   * @param id id
   * @return JdbcPromise
   */
  public ModelPromise<T> findById(Serializable id) {
    if (tableInfo == null || tableInfo.primaryKey == null) {
      throw new IllegalArgumentException("当前实体没有主键");
    }

    String sql = String.format(SQL_TEMPLATE_SELECT_BY_ID, tableInfo.tableName);
    ModelPromise<T> promise = new ModelPromise<>();
    jdbcClient().querySingleWithParams(sql, new JsonArray().add(id), promise);
    return promise;
  }

  @FunctionalInterface
  public interface Then<S> {

    /**
     * 处理
     *
     * @param result 结果
     */
    void then(S result);
  }

  /**
   * jdbc处理约定
   * 
   * @author wubo
   *
   * @param <T>
   */
  public class JdbcPromise<R> implements Handler<AsyncResult<R>> {

    protected List<Then<R>> successes = new ArrayList<>();
    protected List<Then<Throwable>> fails = new ArrayList<>();

    @Override
    public void handle(AsyncResult<R> event) {
      if (event.succeeded()) {
        R result = event.result();
        for (Then<R> then : successes) {
          then.then(result);
        }
      } else {
        for (Then<Throwable> then : fails) {
          then.then(event.cause());
        }
      }
    }

    /**
     * 成功后续处理
     *
     * @param then 处理逻辑
     * @return promise
     */
    public JdbcPromise<R> succeeded(Then<R> then) {
      successes.add(then);
      return this;
    }

    /**
     * 失败后续处理
     *
     * @param then 处理逻辑
     * @return promise
     */
    public JdbcPromise<R> failed(Then<Throwable> then) {
      fails.add(then);
      return this;
    }

  }

  public class ModelPromise<R> extends JdbcPromise<JsonArray> {

    protected List<Then<R>> successes = new ArrayList<>();

    @Override
    public void handle(AsyncResult<JsonArray> event) {
      if (event.succeeded()) {
        event.result();
        // TODO 转换类型
        R result = null;
        for (Then<R> then : successes) {
          then.then(result);
        }
      } else {
        for (Then<Throwable> then : fails) {
          then.then(event.cause());
        }
      }
    }

    public ModelPromise<R> then(Then<R> then) {
      successes.add(then);
      return this;
    }

    @Override
    public ModelPromise<R> failed(Then<Throwable> then) {
      super.failed(then);
      return this;
    }
  }
}
