package vip.justlive.common.base.io;

import java.util.Properties;
import vip.justlive.common.base.util.PlaceHolderHelper;

/**
 * 配置文件源
 * 
 * @author wubo
 *
 */
@FunctionalInterface
public interface PropertySource {

  /**
   * 获取属性集合
   * 
   * @return
   */
  Properties props();

  /**
   * 获取属性
   * 
   * @param key
   * @return
   */
  default String getProperty(String key) {
    String value = props().getProperty(key);
    if (value == null) {
      return value;
    }
    return PlaceHolderHelper.DEFAULT_HELPER.replacePlaceholders(value, props());
  }

  /**
   * 获取属性，可设置默认值
   * 
   * @param key
   * @param defaultValue
   * @return
   */
  default String getProperty(String key, String defaultValue) {
    String value = props().getProperty(key, defaultValue);
    if (value == null) {
      return value;
    }
    return PlaceHolderHelper.DEFAULT_HELPER.replacePlaceholders(value, props());
  }
}
