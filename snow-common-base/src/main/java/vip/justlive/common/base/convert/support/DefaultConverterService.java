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
package vip.justlive.common.base.convert.support;

import java.util.List;
import java.util.Map;
import com.google.common.collect.Maps;
import vip.justlive.common.base.convert.Converter;
import vip.justlive.common.base.convert.ConverterFactory;
import vip.justlive.common.base.convert.ConverterRegistry;
import vip.justlive.common.base.convert.ConverterService;
import vip.justlive.common.base.util.Checks;

/**
 * 默认转换服务实现类
 * 
 * @author wubo
 *
 */
public class DefaultConverterService implements ConverterService, ConverterRegistry {

  /**
   * 转换器集合
   */
  private Map<ConverterTypePair, Converter<Object, Object>> converters = Maps.newHashMap();

  @SuppressWarnings("unchecked")
  @Override
  public ConverterRegistry addConverter(Converter<?, ?> converter) {
    Checks.notNull(converter);
    converters.put(converter.pair(), (Converter<Object, Object>) converter);
    return this;
  }

  @Override
  public ConverterRegistry addConverterFactory(ConverterFactory<?, ?> factory) {
    Checks.notNull(factory);
    List<Converter<Object, Object>> list = factory.converters();
    for (Converter<?, ?> converter : list) {
      addConverter(converter);
    }
    return this;
  }

  @Override
  public boolean canConverter(Class<?> source, Class<?> target) {
    return converters.containsKey(ConverterTypePair.create(source, target));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T convert(Object source, Class<T> targetType) {
    Converter<Object, Object> converter =
        converters.get(ConverterTypePair.create(source.getClass(), targetType));

    return (T) Checks.notNull(converter).convert(source);
  }

}
