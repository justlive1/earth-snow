package vip.justlive.common.base.convert;

import vip.justlive.common.base.convert.support.ConverterTypePair;

/**
 * 类型转换器
 * 
 * @author wubo
 *
 * @param <S>
 * @param <T>
 */
public interface Converter<S, T> {


  /**
   * 类型转换
   * 
   * @param source
   * @return
   */
  T convert(S source);

  /**
   * 获取类型对
   * 
   * @return
   */
  default ConverterTypePair pair() {
    return null;
  }
}
