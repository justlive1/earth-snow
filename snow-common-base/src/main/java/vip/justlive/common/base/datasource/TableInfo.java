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
package vip.justlive.common.base.datasource;

import java.lang.reflect.Field;
import java.util.List;
import lombok.Data;

/**
 * 表信息
 * 
 * @author wubo
 *
 */
@Data
public class TableInfo {

  public static final String COLUMN_SEPARATOR = ",";
  public static final String COLUMN_SEAT = "?";
  public static final String COLUMN_EQUAL = " = ";
  public static final String COLUMN_AND = " and ";

  /**
   * 表名
   */
  protected String tableName;

  /**
   * 主键列
   */
  protected ColumnInfo primaryKey;

  /**
   * 列
   */
  protected List<ColumnInfo> columns;

  /**
   * 查询属性列
   */
  protected String baseColumnList;

  public String getBaseColumnList() {
    if (baseColumnList == null) {
      StringBuilder sb = new StringBuilder();
      for (ColumnInfo column : columns) {
        sb.append(COLUMN_SEPARATOR).append(column.columnName);
      }
      if (sb.length() > 0) {
        sb.deleteCharAt(0);
      }
      this.baseColumnList = sb.toString();
    }
    return baseColumnList;
  }

  /**
   * 列信息
   * 
   * @author wubo
   *
   */
  @Data
  public static class ColumnInfo {

    /**
     * 列名
     */
    protected String columnName;

    /**
     * 属性名
     */
    protected String fieldName;

    /**
     * 是否主键
     */
    protected boolean primaryKey;

    /**
     * 属性类型
     */
    protected Class<?> type;

    /**
     * field
     */
    protected Field field;
  }
}
