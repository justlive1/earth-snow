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
package vip.justlive.common.base.logger;

/**
 * 日志接口
 * 
 * @author wubo
 *
 */
public interface Track {

  /**
   * 请求入口 -> access
   * 
   * @param format
   * @param args
   */
  void request(String format, Object... args);

  /**
   * 请求返回 -> access
   * 
   * @param format
   * @param args
   */
  void response(String format, Object... args);

  /**
   * 服务访问 -> service
   * 
   * @param format
   * @param args
   */
  void service(String format, Object... args);

  /**
   * 网关访问 -> gateway
   * 
   * @param format
   * @param args
   */
  void gateway(String format, Object... args);

  /**
   * 批处理 -> batch
   * 
   * @param format
   * @param args
   */
  void batch(String format, Object... args);

}
