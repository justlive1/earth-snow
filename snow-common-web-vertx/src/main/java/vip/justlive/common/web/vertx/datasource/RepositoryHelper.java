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

import org.apache.commons.beanutils.PropertyUtils;
import io.vertx.core.json.JsonArray;
import vip.justlive.common.base.datasource.TableInfo;
import vip.justlive.common.base.datasource.TypeHandlerHelper;
import vip.justlive.common.base.exception.Exceptions;

/**
 * Repository帮助类
 * 
 * @author wubo
 *
 */
public class RepositoryHelper {

  public static final String SQL_TEMPLATE_SELECT_BY_ID = "select %s from %s where %s = ?";
  public static final String SQL_TEMPLATE_SELECT_BY_MODEL = "select %s from %s where 1 = 1 %s";
  public static final String SQL_TEMPLATE_INSERT = "insert into %s (%s) values (%s)";

  RepositoryHelper() {}

  /**
   * 校验
   * 
   * @param tableInfo 表信息
   */
  public static void check(TableInfo tableInfo) {
    if (tableInfo == null || tableInfo.getPrimaryKey() == null) {
      throw new IllegalArgumentException("当前实体没有主键");
    }
  }

  /**
   * 校验
   * 
   * @param tableInfo 表信息
   * @param model 实体
   */
  public static <T> void check(TableInfo tableInfo, T model) {
    check(tableInfo);
    if (model == null) {
      throw new IllegalArgumentException("model cannot be null");
    }
  }

  /**
   * 解析select
   * 
   * @param model 实体
   * @param jsonArray 参数
   * @param tableInfo 表信息
   * @param <T> 泛型
   * @return sql
   */
  public static <T> String parseSelect(T model, JsonArray jsonArray, TableInfo tableInfo) {
    StringBuilder sb = new StringBuilder();
    for (TableInfo.ColumnInfo column : tableInfo.getColumns()) {
      if (!column.getField().isAccessible()) {
        column.getField().setAccessible(true);
      }
      try {
        Object value = column.getField().get(model);
        if (value != null) {
          sb.append(TableInfo.COLUMN_AND).append(column.getColumnName())
              .append(TableInfo.COLUMN_EQUAL).append(TableInfo.COLUMN_SEAT);
          jsonArray.add(value);
        }
      } catch (Exception e) {
        throw Exceptions.wrap(e);
      }
    }
    return String.format(SQL_TEMPLATE_SELECT_BY_MODEL, tableInfo.getBaseColumnList(),
        tableInfo.getTableName(), sb.toString());
  }

  /**
   * 解析insert
   * 
   * @param model 实体
   * @param jsonArray 参数
   * @param tableInfo 表信息
   * @param <T> 泛型
   * @return sql
   */
  public static <T> String parseInsert(T model, JsonArray jsonArray, TableInfo tableInfo) {
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

  /**
   * 类型转换
   * 
   * @param jsonArray jsonArray
   * @param tableInfo 表信息
   * @param clazz model类
   * @return model实体
   */
  public static <T> T convert(JsonArray jsonArray, TableInfo tableInfo, Class<T> clazz) {
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

}
