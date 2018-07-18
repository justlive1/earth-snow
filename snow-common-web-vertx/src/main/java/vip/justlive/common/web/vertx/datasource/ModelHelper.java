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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.common.base.CaseFormat;
import vip.justlive.common.base.annotation.Column;
import vip.justlive.common.base.annotation.Id;
import vip.justlive.common.base.annotation.Table;
import vip.justlive.common.base.util.ReflectUtils;

/**
 * model帮助类
 * 
 * @author wubo
 *
 */
public class ModelHelper {

  ModelHelper() {}

  private static final Map<Class<?>, TableInfo> CACHE = new ConcurrentHashMap<>();

  /**
   * 获取实体类绑定的table信息
   * 
   * @param clazz 类
   * @return TableInfo
   */
  public static TableInfo getTableInfo(Class<?> clazz) {
    TableInfo tableInfo = CACHE.get(clazz);
    if (tableInfo != null) {
      return tableInfo;
    }
    if (!clazz.isAnnotationPresent(Table.class)) {
      throw new IllegalArgumentException("缺失 @Table 注解");
    }

    tableInfo = new TableInfo();

    Table table = clazz.getAnnotation(Table.class);
    tableInfo.tableName = table.name();
    if (tableInfo.tableName.length() == 0) {
      tableInfo.tableName =
          CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName());
    }
    tableInfo.columns = new ArrayList<>();
    Field[] fields = ReflectUtils.getAllDeclaredFields(clazz);
    for (Field field : fields) {
      if (field.isAnnotationPresent(Column.class)) {
        TableInfo.ColumnInfo columnInfo = getColumnInfo(field);
        if (columnInfo.primaryKey) {
          tableInfo.primaryKey = columnInfo;
        }
        tableInfo.columns.add(columnInfo);
      }
    }
    CACHE.putIfAbsent(clazz, tableInfo);
    return CACHE.get(clazz);
  }

  /**
   * 获取ColumnInfo
   * 
   * @param field 属性
   * @return ColumnInfo
   */
  public static TableInfo.ColumnInfo getColumnInfo(Field field) {
    if (field.isAnnotationPresent(Column.class)) {
      Column column = field.getAnnotation(Column.class);
      TableInfo.ColumnInfo columnInfo = new TableInfo.ColumnInfo();
      columnInfo.columnName = column.name();
      if (columnInfo.columnName.length() == 0) {
        columnInfo.columnName =
            CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
      }
      columnInfo.fieldName = field.getName();
      columnInfo.primaryKey = field.isAnnotationPresent(Id.class);
      columnInfo.type = field.getType();
      return columnInfo;
    }
    throw new IllegalArgumentException("缺失 @Column 注解");
  }
}
