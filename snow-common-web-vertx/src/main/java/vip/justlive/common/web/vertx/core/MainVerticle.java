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
package vip.justlive.common.web.vertx.core;

import java.util.Set;
import org.reflections.Reflections;
import io.vertx.core.AbstractVerticle;
import vip.justlive.common.base.constant.BaseConstants;
import vip.justlive.common.base.ioc.Ioc;
import vip.justlive.common.base.support.ConfigFactory;
import vip.justlive.common.web.vertx.annotation.VertxVerticle;

/**
 * 主单元
 *
 * @author wubo
 */
public class MainVerticle extends AbstractVerticle {

  static {
    ConfigFactory.loadProperties(BaseConstants.CONFIG_PATH);
    String override = ConfigFactory.getProperty(BaseConstants.CONFIG_OVERRIDE_FILE_KEY);
    if (override != null && override.length() > 0) {
      ConfigFactory.loadProperties(override.split(BaseConstants.COMMA_SEPARATOR));
    }
    String packages = ConfigFactory.getProperty(BaseConstants.IOC_SCAN_KEY);
    if (packages != null) {
      Ioc.install(packages.split(BaseConstants.COMMA_SEPARATOR));
    } else {
      Ioc.install();
    }
  }

  @Override
  public void start() {

    String location = ConfigFactory.getProperty(BaseConstants.VERTICLE_PATH_KEY, "vip.justlive");
    Reflections ref = new Reflections(location);
    Set<Class<?>> verticles = ref.getTypesAnnotatedWith(VertxVerticle.class);
    for (Class<?> clazz : verticles) {
      vertx.deployVerticle(clazz.getName());
    }
  }
}
