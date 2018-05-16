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

import java.io.IOException;
import java.util.Properties;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import vip.justlive.common.base.exception.Exceptions;
import vip.justlive.common.base.util.PlaceHolderHelper;

/**
 * properties包装类<br>
 * 在不能使用@Value注解时使用
 * 
 * @author wubo
 *
 */
public class PropertiesWrapper {

  private static final PlaceHolderHelper HELPER = new PlaceHolderHelper(
      PlaceHolderHelper.DEFAULT_PLACEHOLDER_PREFIX, PlaceHolderHelper.DEFAULT_PLACEHOLDER_SUFFIX,
      PlaceHolderHelper.DEFAULT_VALUE_SEPARATOR, true);

  private Properties props;

  public PropertiesWrapper(String path) {

    try {

      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      PropertiesFactoryBean factory = new PropertiesFactoryBean();
      factory.setLocations(resolver.getResources(path));
      factory.setIgnoreResourceNotFound(true);
      factory.setFileEncoding("utf-8");
      factory.afterPropertiesSet();
      this.props = factory.getObject();

    } catch (IOException e) {
      throw Exceptions.wrap(e);
    }
  }

  public String getProperty(String key) {
    String value = props.getProperty(key);
    if (value == null) {
      return value;
    }
    return HELPER.replacePlaceholders(value, props);
  }

  public String getProperty(String key, String defaultValue) {
    String value = props.getProperty(key, defaultValue);
    if (value == null) {
      return value;
    }
    return HELPER.replacePlaceholders(value, props);
  }

}
