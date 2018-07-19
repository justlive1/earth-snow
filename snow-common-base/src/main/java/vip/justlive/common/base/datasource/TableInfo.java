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

  protected String tableName;

  protected ColumnInfo primaryKey;

  protected List<ColumnInfo> columns;

  /**
   * 列信息
   * 
   * @author wubo
   *
   */
  @Data
  public static class ColumnInfo {

    protected String columnName;

    protected String fieldName;

    protected boolean primaryKey;

    protected Class<?> type;
  }
}
