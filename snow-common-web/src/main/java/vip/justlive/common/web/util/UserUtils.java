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
package vip.justlive.common.web.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import com.google.common.collect.Maps;
import vip.justlive.common.base.exception.Exceptions;
import vip.justlive.common.base.util.ReflectUtils;

/**
 * 用户相关工具类
 * 
 * @author wubo
 *
 */
public class UserUtils {

  private static final String CAS_USERUTILS_CLASS =
      "justlive.earth.breeze.storm.cas.client.util.CasUserUtils";
  private static final String SECURITY_USERUTILS_CLASS =
      "justlive.earth.breeze.storm.cas.client.security.util.SercurityUserUtils";
  private static final String SHIRO_USERUTILS_CLASS =
      "justlive.earth.breeze.storm.cas.client.shiro.util.ShiroUserUtils";

  private static final Map<String, Method> CACHE_MAP = Maps.newHashMap();

  private UserUtils() {}

  /**
   * 获取当前登陆用户名
   * 
   * @return
   */
  public static String loginUserName() {

    return invoke();
  }

  private static String invoke() {

    String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
    Method method = CACHE_MAP.get(methodName);
    if (method == null) {

      Class<?> clazz = null;
      if (ClassUtils.isPresent(CAS_USERUTILS_CLASS, UserUtils.class.getClassLoader())) {
        clazz = ReflectUtils.forName(CAS_USERUTILS_CLASS);
      } else if (ClassUtils.isPresent(SECURITY_USERUTILS_CLASS, UserUtils.class.getClassLoader())) {
        clazz = ReflectUtils.forName(SECURITY_USERUTILS_CLASS);
      } else if (ClassUtils.isPresent(SHIRO_USERUTILS_CLASS, UserUtils.class.getClassLoader())) {
        clazz = ReflectUtils.forName(SHIRO_USERUTILS_CLASS);
      }

      if (clazz == null) {
        return null;
      }

      method = ReflectionUtils.findMethod(clazz, methodName);
      CACHE_MAP.put(methodName, method);
    }
    return invoke(method, null);
  }

  @SuppressWarnings("unchecked")
  private static <T> T invoke(Method method, Object obj, Object... args) {
    try {
      return (T) method.invoke(obj, args);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw Exceptions.wrap(e);
    }
  }

}
