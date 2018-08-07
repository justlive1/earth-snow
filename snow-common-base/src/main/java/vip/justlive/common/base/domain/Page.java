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
package vip.justlive.common.base.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页
 * 
 * @author wubo
 *
 * @param <T> 泛型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {

  /**
   * 第几页
   */
  private Integer pageIndex;

  /**
   * 每页条数
   */
  private Integer pageSize;

  /**
   * 总计
   */
  private Long totalNumber;

  /**
   * 数据集合
   */
  private List<T> items;
}
