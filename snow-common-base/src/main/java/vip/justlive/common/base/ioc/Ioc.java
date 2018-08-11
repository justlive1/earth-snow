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
package vip.justlive.common.base.ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.reflections.Reflections;
import vip.justlive.common.base.annotation.Inject;
import vip.justlive.common.base.annotation.Named;
import vip.justlive.common.base.annotation.Singleton;

/**
 * Ioc
 * 
 * @author wubo
 *
 */
public class Ioc {

  Ioc() {}

  private static final ConcurrentMap<Class<?>, ConcurrentMap<String, Object>> BEANS =
      new ConcurrentHashMap<>();

  private static final AtomicInteger TODO_INJECT = new AtomicInteger();

  private static final Object EMPTY = new Object();

  private static volatile boolean require = true;

  /**
   * 加载托管bean
   * 
   * @param packages 扫描包路径
   */
  public static void install(String... packages) {
    scan(packages);
    ioc();
  }

  /**
   * 根据类型获取bean
   * 
   * @param clazz 类
   * @return bean
   */
  public static <T> T getBean(Class<T> clazz) {
    return getBean(clazz.getName(), clazz);
  }

  /**
   * 根据类型和名称获取bean
   * 
   * @param name beanId
   * @param clazz 类
   * @return bean
   */
  public static <T> T getBean(String name, Class<T> clazz) {
    ConcurrentMap<String, Object> map = BEANS.get(clazz);
    if (map != null) {
      return clazz.cast(map.get(name));
    }
    return null;
  }

  static void scan(String... packages) {
    Reflections ref = new Reflections("vip.justlive", packages);
    for (Class<?> clazz : ref.getTypesAnnotatedWith(Singleton.class)) {
      ConcurrentMap<String, Object> map = BEANS.get(clazz);
      if (map == null) {
        BEANS.putIfAbsent(clazz, new ConcurrentHashMap<>(1, 1f));
      }

      Singleton singleton = clazz.getAnnotation(Singleton.class);
      String beanName = singleton.value();
      if (beanName == null || beanName.length() == 0) {
        beanName = clazz.getName();
      }
      if (BEANS.get(clazz).putIfAbsent(beanName, EMPTY) != null) {
        throw new IllegalArgumentException(String.format("[%s] 名称已被定义", beanName));
      }
      TODO_INJECT.incrementAndGet();
    }
  }

  static void ioc() {
    int pre = TODO_INJECT.get();
    while (TODO_INJECT.get() > 0) {
      instance();
      int now = TODO_INJECT.get();
      if (now > 0 && now == pre) {
        if (!require) {
          throw new IllegalStateException("发生循环依赖或者缺失Bean ");
        } else {
          require = false;
        }
      }
      pre = now;
    }
  }

  static void instance() {
    BEANS.forEach((clazz, value) -> value.forEach((name, v) -> {
      if (v == EMPTY) {
        Object inst = instance(clazz);
        if (inst != null) {
          BEANS.get(clazz).put(name, inst);
          TODO_INJECT.decrementAndGet();
        }
      }
    }));
  }

  static Object instance(Class<?> clazz) {
    Constructor<?>[] constructors = clazz.getConstructors();
    for (Constructor<?> constructor : constructors) {
      if (constructor.isAnnotationPresent(Inject.class)) {
        return dependencyInstance(clazz, constructor);
      }
    }
    return nonDependencyInstance(clazz);
  }

  static Object nonDependencyInstance(Class<?> clazz) {
    try {
      return clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalArgumentException(String.format("[%s]无参构造实例对象失败", clazz), e);
    }
  }

  static Object dependencyInstance(Class<?> clazz, Constructor<?> constructor) {
    Inject inject = constructor.getAnnotation(Inject.class);
    Parameter[] params = constructor.getParameters();
    Object[] args = new Object[params.length];
    boolean canInst = fillParams(params, args, inject.required());
    if (canInst) {
      try {
        return constructor.newInstance(args);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException e) {
        throw new IllegalArgumentException(String.format("[%s]有参构造实例对象失败", clazz), e);
      }
    }
    return null;
  }

  static Object getVal(Parameter param, ConcurrentMap<String, Object> map) {
    Object val;
    if (param.isAnnotationPresent(Named.class)) {
      Named named = param.getAnnotation(Named.class);
      val = map.get(named.value());
    } else {
      val = map.get(param.getType().getName());
      if (val == null && !map.isEmpty()) {
        val = map.values().iterator().next();
      }
    }
    return val;
  }

  static boolean fillParams(Parameter[] params, Object[] args, boolean required) {
    for (int i = 0; i < params.length; i++) {
      ConcurrentMap<String, Object> map = BEANS.get(params[i].getType());
      if (map != null) {
        args[i] = getVal(params[i], map);
      }
      if (args[i] == EMPTY || (require && args[i] == null) || (args[i] == null && required)) {
        return false;
      }
    }
    return true;
  }
}
