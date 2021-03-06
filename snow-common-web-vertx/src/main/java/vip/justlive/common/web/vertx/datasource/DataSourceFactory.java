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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.JustLive;

/**
 * 数据源工厂类
 * 
 * @author wubo
 *
 */
public class DataSourceFactory {

  DataSourceFactory() {}

  private static final Map<Class<?>, JDBCClient> CLIENTS = new ConcurrentHashMap<>(4);

  /**
   * 共享jdbcClient
   *
   * @return jdbcClient
   */
  public static JDBCClient sharedJdbcClient() {
    DataSourceConf conf = ConfigFactory.load(DataSourceConf.class);
    JsonObject json = JsonObject.mapFrom(conf);
    return JDBCClient.createShared(JustLive.vertx(), json);
  }

  /**
   * 共享单例jdbcClient
   *
   * @return jdbcClient
   */
  public static JDBCClient sharedSingleJdbcClient() {
    return sharedSingleJdbcClient(DataSourceFactory.class);
  }

  /**
   * 共享单例jdbcClient
   *
   * @param clazz 类
   * @return jdbcClient
   */
  public static JDBCClient sharedSingleJdbcClient(Class<?> clazz) {
    JDBCClient client = CLIENTS.get(clazz);
    if (client == null) {
      CLIENTS.putIfAbsent(clazz, sharedJdbcClient());
    }
    return CLIENTS.get(clazz);
  }

}
