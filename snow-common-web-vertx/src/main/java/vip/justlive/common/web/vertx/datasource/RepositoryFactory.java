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
import vip.justlive.common.base.exception.Exceptions;

/**
 * Repository工厂类
 * 
 * @author wubo
 *
 */
public class RepositoryFactory {

  RepositoryFactory() {}

  private static final Map<Class<? extends Repository<?>>, Repository<?>> REPOSITORIES =
      new ConcurrentHashMap<>();

  /**
   * 
   * 获取Repository
   *
   * @param clazz Repository子类
   * @param <T> 泛型
   * @param <R> 泛型
   * @return Repository
   */
  public static <T extends Repository<R>, R> T repository(Class<T> clazz) {
    Repository<?> repository = REPOSITORIES.get(clazz);
    if (repository != null) {
      return clazz.cast(repository);
    }
    try {
      T obj = clazz.newInstance();
      REPOSITORIES.putIfAbsent(clazz, obj);
    } catch (InstantiationException | IllegalAccessException e) {
      throw Exceptions.wrap(e);
    }
    return clazz.cast(REPOSITORIES.get(clazz));
  }
}
