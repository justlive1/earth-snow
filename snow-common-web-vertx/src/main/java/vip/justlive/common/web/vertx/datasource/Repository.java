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
import org.apache.commons.beanutils.PropertyUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import vip.justlive.common.base.datasource.ModelHelper;
import vip.justlive.common.base.datasource.TableInfo;
import vip.justlive.common.base.datasource.TypeHandlerHelper;
import vip.justlive.common.base.exception.Exceptions;

/**
 * Repository
 * 
 * @author wubo
 *
 * @param <T> 泛型
 */
@Slf4j
public class Repository<T> {

  private static final String SQL_TEMPLATE_SELECT_BY_ID = "select %s from %s where id = ?";
  private static final String SQL_TEMPLATE_INSERT = "insert into %s (%s) values (%s)";

  protected Class<T> clazz;

  private final TableInfo tableInfo;

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
  public ModelPromise findById(Serializable id) {
    check();
    String sql = String.format(SQL_TEMPLATE_SELECT_BY_ID, tableInfo.getBaseColumnList(),
        tableInfo.getTableName());
    ModelPromise promise = new ModelPromise();
    jdbcClient().querySingleWithParams(sql, new JsonArray().add(id), promise);
    return promise;
  }

  /**
   * 持久化
   * 
   * @param model 实体
   * @return JdbcPromise
   */
  public JdbcPromise<UpdateResult> save(T model) {
    check();
    if (model == null) {
      throw new IllegalArgumentException("model cannot be null");
    }

    JdbcPromise<UpdateResult> promise = new JdbcPromise<>();
    JsonArray jsonArray = new JsonArray();
    String sql = parse(model, jsonArray);
    jdbcClient().updateWithParams(sql, jsonArray, promise);

    return promise;
  }


  private void check() {
    if (tableInfo == null || tableInfo.getPrimaryKey() == null) {
      throw new IllegalArgumentException("当前实体没有主键");
    }
  }

  private String parse(T model, JsonArray jsonArray) {
    StringBuilder sb = new StringBuilder();
    StringBuilder seat = new StringBuilder();
    for (TableInfo.ColumnInfo column : tableInfo.getColumns()) {
      if (!column.getField().isAccessible()) {
        column.getField().setAccessible(true);
      }
      try {
        Object value = column.getField().get(model);
        if (value != null) {
          sb.append(TableInfo.COLUMN_SEPARATOR).append(column.getColumnName());
          seat.append(TableInfo.COLUMN_SEPARATOR).append(TableInfo.COLUMN_SEAT);
          jsonArray.add(value);
        }
      } catch (Exception e) {
        throw Exceptions.wrap(e);
      }
    }
    if (sb.length() > 0) {
      sb.deleteCharAt(0);
      seat.deleteCharAt(0);
    }
    return String.format(SQL_TEMPLATE_INSERT, tableInfo.getTableName(), sb.toString(),
        seat.toString());
  }

  private T convert(JsonArray jsonArray) {
    try {
      T obj = clazz.newInstance();
      for (int i = 0, len = tableInfo.getColumns().size(); i < len; i++) {
        TableInfo.ColumnInfo column = tableInfo.getColumns().get(i);
        Object fieldValue = TypeHandlerHelper.getResult(jsonArray, i, column.getType());
        PropertyUtils.setProperty(obj, column.getFieldName(), fieldValue);
      }
      return obj;
    } catch (Exception e) {
      throw Exceptions.wrap(e);
    }
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
   * @param <R>
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
        log.error("jdbc option error ", event.cause());
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

  /**
   * Model约定
   * 
   * @author wubo
   *
   */
  public class ModelPromise extends JdbcPromise<JsonArray> {

    protected List<Then<T>> successes = new ArrayList<>();

    @Override
    public void handle(AsyncResult<JsonArray> event) {
      if (event.succeeded()) {
        T result = convert(event.result());
        for (Then<T> then : successes) {
          then.then(result);
        }
      } else {
        for (Then<Throwable> then : fails) {
          then.then(event.cause());
        }
      }
    }

    public ModelPromise then(Then<T> then) {
      successes.add(then);
      return this;
    }
  }

}
