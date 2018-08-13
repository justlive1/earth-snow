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

import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.reflections.Reflections;
import lombok.extern.slf4j.Slf4j;
import vip.justlive.common.base.annotation.Configuration;
import vip.justlive.common.base.annotation.Singleton;

/**
 * Ioc <br>
 * 当前只支持构造方法注入<br>
 * 原因: 易于切换不同ioc实现
 * 
 * @author wubo
 *
 */
@Slf4j
public class Ioc {

  Ioc() {}

  private static final AtomicInteger TODO_INJECT = new AtomicInteger();

  private static Strategy strategy = new ConstructorStrategy();

  /**
   * 加载托管bean
   * 
   * @param packages 扫描包路径
   */
  public static void install(String... packages) {
    scan(packages);
    ioc();
    merge();
  }

  /**
   * 实例bean， 通过构造函数实例
   * 
   * @param clazz 类
   * @return 实例
   */
  public static Object instanceBean(Class<?> clazz) {
    return strategy.instance(clazz);
  }

  static void scan(String... packages) {
    Reflections ref = new Reflections("vip.justlive", packages);
    // Configuration
    for (Class<?> clazz : ref.getTypesAnnotatedWith(Configuration.class)) {
      configBeans(clazz);
    }
    // Singleton
    for (Class<?> clazz : ref.getTypesAnnotatedWith(Singleton.class)) {
      Singleton singleton = clazz.getAnnotation(Singleton.class);
      String beanName = singleton.value();
      if (beanName == null || beanName.length() == 0) {
        beanName = clazz.getName();
      }
      BeanStore.seize(clazz, beanName);
      TODO_INJECT.incrementAndGet();
    }
  }

  static void configBeans(Class<?> clazz) {
    Object obj;
    try {
      obj = clazz.newInstance();
    } catch (Exception e) {
      throw new IllegalStateException(String.format("@Configuration注解的类[%s]无法实例化", clazz), e);
    }
    for (Method method : clazz.getDeclaredMethods()) {
      if (method.isAnnotationPresent(Singleton.class)) {
        if (method.getParameterCount() > 0) {
          throw new IllegalStateException("@Configuration下实例Bean不支持有参方式");
        }
        method.setAccessible(true);
        Object bean;
        try {
          bean = method.invoke(obj);
        } catch (Exception e) {
          throw new IllegalStateException("@Configuration下实例方法出错", e);
        }
        Singleton singleton = method.getAnnotation(Singleton.class);
        String name = singleton.value();
        if (name.length() == 0) {
          name = method.getName();
        }
        BeanStore.putBean(name, bean);
      }
    }
  }

  static void ioc() {
    int pre = TODO_INJECT.get();
    while (TODO_INJECT.get() > 0) {
      instance();
      int now = TODO_INJECT.get();
      if (now > 0 && now == pre) {
        if (!strategy.isRequired()) {
          if (log.isDebugEnabled()) {
            log.debug("ioc失败 出现循环依赖或缺失Bean TODO_INJECT={}, beans={}", now, BeanStore.BEANS);
          }
          throw new IllegalStateException("发生循环依赖或者缺失Bean ");
        } else {
          strategy.nonRequired();
        }
      }
      pre = now;
    }
  }

  static void instance() {
    BeanStore.BEANS.forEach((clazz, value) -> value.forEach((name, v) -> {
      if (v == BeanStore.EMPTY) {
        Object inst = instanceBean(clazz);
        if (inst != null) {
          BeanStore.putBean(name, inst);
          TODO_INJECT.decrementAndGet();
        }
      }
    }));
  }

  static void merge() {
    for (Entry<Class<?>, ConcurrentMap<String, Object>> entry : BeanStore.BEANS.entrySet()) {
      Class<?> clazz = entry.getKey();
      BeanStore.merge(clazz, entry.getValue().values().iterator().next());
    }
  }

}
