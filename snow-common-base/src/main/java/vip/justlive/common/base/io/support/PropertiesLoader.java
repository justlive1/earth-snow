package vip.justlive.common.base.io.support;

import java.io.IOException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import vip.justlive.common.base.exception.Exceptions;
import vip.justlive.common.base.io.PropertySource;
import vip.justlive.common.base.io.SourceResource;

/**
 * properties配置文件加载器<br>
 * 支持classpath下配置文件，例如 classpath:/config/dev.properties,classpath*:/config/*.properties<br>
 * 支持文件系统下配置文件，例如 file:/home/dev.properties, file:D:/conf/dev.properties<br>
 * 支持配置文件中使用${k1:key}
 * 
 * @author wubo
 *
 */
@Slf4j
public class PropertiesLoader extends AbstractResourceLoader implements PropertySource {

  /**
   * 配置路径
   */
  private String[] locations;

  private Properties props = new Properties();

  /**
   * 使用路径创建{@code PropertiesLoader}
   * 
   * @param locations
   */
  public PropertiesLoader(String... locations) {
    this(ClassLoader.getSystemClassLoader(), locations);
  }

  /**
   * 使用路径创建{@code PropertiesLoader}
   * 
   * @param loader
   * @param locations
   */
  public PropertiesLoader(ClassLoader loader, String... locations) {
    this.locations = locations;
    this.loader = loader;
  }

  /**
   * 获取属性值
   * 
   * @return
   */
  public Properties props() {
    if (!this.ready) {
      this.init();
    }
    return this.props;
  }

  @Override
  public void init() {
    if (this.ready) {
      return;
    }
    this.ready = true;
    this.resources.addAll(this.parse(this.locations));
    for (SourceResource resource : this.resources) {
      try {
        props.load(this.getReader(resource));
      } catch (IOException e) {
        if (log.isDebugEnabled()) {
          log.debug("resource [{}] read error", resource.path(), e);
        }
        if (!ignoreNotFound) {
          throw Exceptions.wrap(e);
        }
      }
    }
  }

}
