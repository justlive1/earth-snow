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

/**
 * 类型转换
 * 
 * @author wubo
 *
 * @param <R> 泛型
 * @param <T> 泛型
 */
public interface TypeHandler<R, T> {

  /**
   * 获取入类型
   * 
   * @return class
   */
  Class<R> getInType();

  /**
   * 获取出类型
   * 
   * @return class
   */
  Class<T> getOutType();

  /**
   * 获取结果
   * 
   * @param resultSet 结果集
   * @param columnName 列明
   * @return 结果
   */
  default T getResult(R resultSet, String columnName) {
    throw new UnsupportedOperationException();
  }

  /**
   * 获取结果
   * 
   * @param resultSet 结果集
   * @param index 下标
   * @return 结果
   */
  default T getResult(R resultSet, int index) {
    throw new UnsupportedOperationException();
  }

}
