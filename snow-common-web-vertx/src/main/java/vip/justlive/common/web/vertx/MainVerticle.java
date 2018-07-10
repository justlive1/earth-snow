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

import java.util.Set;
import org.reflections.Reflections;
import io.vertx.core.AbstractVerticle;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.annotation.VertxVerticle;

/**
 * 主单元
 *
 * @author wubo
 */
public class MainVerticle extends AbstractVerticle {

  static {
    ConfigFactory.loadProperties("classpath:/config/*.properties");
  }

  @Override
  public void start() {

    String location = ConfigFactory.getProperty("main.verticle.path", "vip.justlive");
    Reflections ref = new Reflections(location);
    Set<Class<?>> verticles = ref.getTypesAnnotatedWith(VertxVerticle.class);
    for (Class<?> clazz : verticles) {
      vertx.deployVerticle(clazz.getName());
    }
  }
}
