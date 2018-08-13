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
import java.util.concurrent.ConcurrentMap;
import vip.justlive.common.base.annotation.Inject;
import vip.justlive.common.base.annotation.Named;

/**
 * 构造方法策略
 * 
 * @author wubo
 *
 */
public class ConstructorStrategy implements Strategy {

  private volatile boolean require = true;

  @Override
  public Object instance(Class<?> clazz) {
    Constructor<?>[] constructors = clazz.getConstructors();
    for (Constructor<?> constructor : constructors) {
      if (constructor.isAnnotationPresent(Inject.class)) {
        return dependencyInstance(clazz, constructor);
      }
    }
    return nonDependencyInstance(clazz);
  }

  Object nonDependencyInstance(Class<?> clazz) {
    try {
      return clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalArgumentException(String.format("[%s]无参构造实例对象失败", clazz), e);
    }
  }

  Object dependencyInstance(Class<?> clazz, Constructor<?> constructor) {
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

  Object getVal(Parameter param, ConcurrentMap<String, Object> map) {
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

  boolean fillParams(Parameter[] params, Object[] args, boolean required) {
    for (int i = 0; i < params.length; i++) {
      ConcurrentMap<String, Object> map = BeanStore.BEANS.get(params[i].getType());
      if (map != null) {
        args[i] = getVal(params[i], map);
      }
      boolean notInst = (args[i] == BeanStore.EMPTY) || (require && args[i] == null)
          || (args[i] == null && required);
      if (notInst) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void nonRequired() {
    require = false;
  }

  @Override
  public boolean isRequired() {
    return require;
  }
}
