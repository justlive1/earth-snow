package justlive.earth.breeze.snow.common.base.logger;

/**
 * 日志接口
 * 
 * @author wubo
 *
 */
public interface Track {

  /**
   * 请求入口 -> access
   * 
   * @param format
   * @param args
   */
  void request(String format, Object... args);

  /**
   * 请求返回 -> access
   * 
   * @param format
   * @param args
   */
  void response(String format, Object... args);

  /**
   * 服务访问 -> service
   * 
   * @param format
   * @param args
   */
  void service(String format, Object... args);

  /**
   * 网关访问 -> gateway
   * 
   * @param format
   * @param args
   */
  void gateway(String format, Object... args);

  /**
   * 批处理 -> batch
   * 
   * @param format
   * @param args
   */
  void batch(String format, Object... args);

}
