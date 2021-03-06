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
package vip.justlive.common.web.vertx;

import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.SLF4JLogDelegateFactory;

/**
 * just live
 *
 * @author wubo
 */
public class JustLive extends Launcher {

  private static Vertx VERTX;

  private JustLive() {}

  /**
   * get single vertx
   *
   * @return vertx
   */
  public static Vertx vertx() {
    if (VERTX == null) {
      VERTX = Vertx.vertx();
    }
    return VERTX;
  }

  /**
   * Main entry point.
   *
   * @param args the user command line arguments.
   */
  public static void main(String[] args) {

    // 使用SLF4J框架
    System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME,
        SLF4JLogDelegateFactory.class.getName());

    new JustLive().dispatch(args);
  }

  @Override
  public void afterStartingVertx(Vertx vertx) {
    JustLive.VERTX = vertx;
  }
}

