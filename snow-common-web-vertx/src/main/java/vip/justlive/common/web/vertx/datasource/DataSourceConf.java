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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import vip.justlive.common.base.annotation.Value;

/**
 * 数据源配置
 * 
 * @author wubo
 *
 */
@Data
public class DataSourceConf {

  /**
   * 数据源提供类
   */
  @JsonProperty("provider_class")
  @Value("${datasource.providerClass}")
  private String providerClass;

  /**
   * 驱动
   */
  @Value("${datasource.driverClassName}")
  private String driverClassName;

  /**
   * 连接串
   */
  @Value("${datasource.jdbcUrl}")
  private String jdbcUrl;

  /**
   * 用户名
   */
  @Value("${datasource.username}")
  private String username;

  /**
   * 密码
   */
  @Value("${datasource.password}")
  private String password;

  /**
   * 最大线程数
   */
  @Value("${datasource.maximumPoolSize:10}")
  private Integer maximumPoolSize;

  /**
   * 最小空闲线程数
   */
  @Value("${datasource.minimumIdle:5}")
  private Integer minimumIdle;

  /**
   * prepStatement缓存数
   */
  @Value("${datasource.prepStmtCacheSize:250}")
  private Integer prepStmtCacheSiz;

  /**
   * prepStatement缓存大小
   */
  @Value("${datasource.prepStmtCacheSqlLimit:2048}")
  private Integer prepStmtCacheSqlLimit;
}
