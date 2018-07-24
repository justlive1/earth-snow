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

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * json返回实体
 * 
 * @author wubo
 * @param <T> 泛型类
 */
@Data
@NoArgsConstructor
public class Response<T> {

  public static final String SUCC = "00000";
  public static final String FAIL = "99999";
  /**
   * 返回结果编码
   */
  private String code;

  /**
   * 结果描述信息
   */
  private String message;

  /**
   * 返回数据
   */
  private T data;

  /**
   * 成功返回
   * 
   * @param <T> 泛型类
   * @return 返回实体
   */
  public static <T> Response<T> success() {
    Response<T> resp = new Response<>();
    resp.setCode(SUCC);
    return resp;
  }

  /**
   * 成功返回
   * 
   * @param data 数据
   * @param <T> 泛型类
   * @return 返回实体
   */
  public static <T> Response<T> success(T data) {
    Response<T> resp = new Response<>();
    resp.setData(data);
    resp.setCode(SUCC);
    return resp;
  }

  /**
   * 失败返回
   * 
   * @param message 消息
   * @param <T> 泛型类
   * @return 返回实体
   */
  public static <T> Response<T> error(String message) {
    Response<T> resp = new Response<>();
    resp.setCode(FAIL);
    resp.setMessage(message);
    return resp;
  }

  /**
   * 失败返回
   * 
   * @param code 编码
   * @param message 消息
   * @param <T> 泛型类
   * @return 返回实体
   */
  public static <T> Response<T> error(String code, String message) {
    Response<T> resp = new Response<>();
    resp.setCode(code);
    resp.setMessage(message);
    return resp;
  }

  /**
   * 是否成功
   * 
   * @return 是否成功
   */
  public boolean isSuccess() {
    return SUCC.equals(code);
  }
}
