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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.reflections.Reflections;
import lombok.extern.slf4j.Slf4j;

/**
 * TypeHandler帮助类
 * 
 * @author wubo
 *
 */
@Slf4j
@SuppressWarnings("rawtypes")
public class TypeHandlerHelper {

  TypeHandlerHelper() {}

  private static final Map<Class<?>, Map<Class<?>, TypeHandler<?, ?>>> HANDLERS =
      new ConcurrentHashMap<>();

  static {
    Reflections ref = new Reflections("vip.justlive");
    Set<Class<? extends TypeHandler>> clazzes = ref.getSubTypesOf(TypeHandler.class);
    for (Class<? extends TypeHandler> clazz : clazzes) {
      try {
        TypeHandler instance = clazz.newInstance();
        registerTypeHandler(instance);
      } catch (InstantiationException | IllegalAccessException e) {
        log.warn("[TypeHandler] register error", e);
      }
    }
  }

  /**
   * 注册类型转换器
   * 
   * @param typeHandler 类型转换
   */
  public static void registerTypeHandler(TypeHandler<?, ?> typeHandler) {
    Map<Class<?>, TypeHandler<?, ?>> map = HANDLERS.get(typeHandler.getOutType());
    if (map == null) {
      HANDLERS.putIfAbsent(typeHandler.getOutType(), new ConcurrentHashMap<>());
    }
    HANDLERS.get(typeHandler.getOutType()).put(typeHandler.getInType(), typeHandler);
  }

  /**
   * 获取结果
   * 
   * @param resultSet 结果集
   * @param index 下标
   * @param clazz 转换类型
   * @param <R> 泛型
   * @param <T> 泛型
   * @return 结果
   */
  public static <R, T> T getResult(R resultSet, int index, Class<T> clazz) {
    Map<Class<?>, TypeHandler<?, ?>> map = HANDLERS.get(clazz);
    if (map == null || !map.containsKey(resultSet.getClass())) {
      throw new IllegalArgumentException(
          String.format("not found TypeHandler for [%s]-[%s]", resultSet.getClass(), clazz));
    }

    @SuppressWarnings("unchecked")
    TypeHandler<R, T> typeHandler = (TypeHandler<R, T>) map.get(resultSet.getClass());
    return typeHandler.getResult(resultSet, index);
  }
}
