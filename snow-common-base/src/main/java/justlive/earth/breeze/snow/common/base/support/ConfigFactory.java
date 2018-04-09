package justlive.earth.breeze.snow.common.base.support;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import justlive.earth.breeze.snow.common.base.annotation.Value;
import justlive.earth.breeze.snow.common.base.convert.support.DefaultConverterService;
import justlive.earth.breeze.snow.common.base.convert.support.StringToBooleanConverter;
import justlive.earth.breeze.snow.common.base.convert.support.StringToCharacterConverter;
import justlive.earth.breeze.snow.common.base.convert.support.StringToNumberConverterFactory;
import justlive.earth.breeze.snow.common.base.exception.Exceptions;
import justlive.earth.breeze.snow.common.base.io.PropertySource;
import justlive.earth.breeze.snow.common.base.io.support.PropertiesLoader;
import justlive.earth.breeze.snow.common.base.util.ReflectUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 配置工厂
 * 
 * @author wubo
 *
 */
@Slf4j
public class ConfigFactory {

  private ConfigFactory() {}

  /**
   * 存储解析过的配置类
   */
  private static final Map<Class<?>, Object> FACTORY = new ConcurrentHashMap<>();

  /**
   * 配置属性集合
   */
  private static final Properties PROPS = new Properties();

  /**
   * 配置资源包装
   */
  private static final PropertySource SOURCE_WRAPPER = () -> PROPS;

  /**
   * 用于生成临时编号
   */
  private static final AtomicLong ATOMIC = new AtomicLong();

  /**
   * 临时编号前缀
   */
  private static final String TMP_PREFIX = "ConfigFactory.tmp.%s";

  /**
   * 类型转换器
   */
  private static final DefaultConverterService CONVERTER_SERVICE = new DefaultConverterService();

  static {
    CONVERTER_SERVICE.addConverter(new StringToBooleanConverter())
        .addConverter(new StringToCharacterConverter())
        .addConverterFactory(new StringToNumberConverterFactory());
  }

  /**
   * 加载配置文件
   * 
   * @param locations
   */
  public static void loadProperties(String... locations) {
    loadProperties(StandardCharsets.UTF_8, true, locations);
  }

  /**
   * 加载配置文件，设置编码和忽略找不到的资源
   * 
   * @param charset
   * @param ignoreNotFound
   * @param locations
   */
  public static void loadProperties(Charset charset, boolean ignoreNotFound, String... locations) {
    PropertiesLoader loader = new PropertiesLoader(locations);
    loader.setCharset(charset);
    loader.setIgnoreNotFound(ignoreNotFound);
    loadProperties(loader);
  }

  /**
   * 加载配置文件，传入配置属性资源
   * 
   * @param source
   */
  public static void loadProperties(PropertySource source) {
    PROPS.putAll(source.props());
  }

  /**
   * 获取配置属性
   * 
   * @param key
   * @return
   */
  public static String getProperty(String key) {
    return SOURCE_WRAPPER.getProperty(key);
  }

  /**
   * 获取配置属性，可设置默认值
   * 
   * @param key
   * @return
   */
  public static String getProperty(String key, String defaultValue) {
    return SOURCE_WRAPPER.getProperty(key, defaultValue);
  }

  /**
   * 加载配置类，需要有{@link Value}注解
   * 
   * @param clazz
   * @return
   */
  public static <T> T load(Class<T> clazz) {
    Object obj = FACTORY.get(clazz);
    if (obj != null) {
      return clazz.cast(obj);
    }

    T val = parse(clazz);
    Object other = FACTORY.putIfAbsent(clazz, val);
    if (other != null) {
      val = clazz.cast(other);
    }
    return val;
  }

  /**
   * 解析
   * 
   * @param clazz
   * @return
   */
  protected static <T> T parse(Class<T> clazz) {
    Field[] fields = ReflectUtils.getAllDeclaredFields(clazz);
    T obj;
    try {
      obj = clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw Exceptions.wrap(e);
    }
    for (Field field : fields) {
      if (field.isAnnotationPresent(Value.class)) {
        Value val = field.getAnnotation(Value.class);
        Object value = getProperty(val.value(), field.getType());
        if (value != null) {
          field.setAccessible(true);
          try {
            field.set(obj, value);
          } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("set value {} to class {} error", value, clazz, e);
          }
        }
      }
    }
    return obj;
  }

  private static Object getProperty(String key, Class<?> type) {
    String tmpKey = String.format(TMP_PREFIX, ATOMIC.getAndIncrement());
    PROPS.setProperty(tmpKey, key);
    String value = getProperty(tmpKey);
    PROPS.remove(tmpKey);
    if (value.getClass() == type) {
      return value;
    }
    return CONVERTER_SERVICE.convert(value, type);
  }

}
