package justlive.earth.breeze.snow.common.base.logger.support;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import justlive.earth.breeze.snow.common.base.logger.Track;
import lombok.Data;

/**
 * 系统日志工具，非web环境
 * 
 * @author wubo
 *
 */
@Data
public class TrackImpl implements Track {

  /**
   * 当前线程UID存储
   */
  static final ThreadLocal<String> UIDS = new ThreadLocal<>();

  /**
   * 记录应用的访问情况的Logger
   */
  protected static final Logger access = LoggerFactory.getLogger("TRACK.ACCESS");

  /**
   * 记录应用服务层调用的Logger
   */
  protected static final Logger service = LoggerFactory.getLogger("TRACK.SERVICE");

  /**
   * 记录应用外部调用的Logger
   */
  protected static final Logger gateway = LoggerFactory.getLogger("TRACK.GATEWAY");

  /**
   * 记录应用批处理的Logger
   */
  protected static final Logger batch = LoggerFactory.getLogger("TRACK.BATCH");

  /**
   * 是否开启访问日志 -> Controller
   */
  private boolean accessEnabled;

  /**
   * 是否开启服务日志 -> Service
   */
  private boolean serviceEnabled;

  /**
   * 是否开启网关日志 -> Gateway
   */
  private boolean gatewayEnabled;

  /**
   * 是否开启批处理日志 -> Batch
   */
  private boolean batchEnabled;

  @Override
  public void request(String format, Object... args) {
    if (accessEnabled) {
      String info = String.format("REQ [%s] [%s] [%s]", uid(), ctx(), format);
      access.info(info, args);
    }
  }

  @Override
  public void response(String format, Object... args) {
    if (accessEnabled) {
      String info = String.format("RES [%s] [%s] [%s]", uid(), ctx(), format);
      access.info(info, args);
    }
  }

  @Override
  public void service(String format, Object... args) {
    if (serviceEnabled) {
      String info = String.format("SRV [%s] [%s] [%s]", uid(), ctx(), format);
      service.info(info, args);
    }
  }

  @Override
  public void gateway(String format, Object... args) {
    if (gatewayEnabled) {
      String info = String.format("GTW [%s] [%s] [%s]", uid(), ctx(), format);
      gateway.info(info, args);
    }
  }

  @Override
  public void batch(String format, Object... args) {
    if (batchEnabled) {
      String info = String.format("BAT [%s] [%s] [%s]", uid(), ctx(), format);
      batch.info(info, args);
    }
  }

  protected String uid() {
    String uid = UIDS.get();
    if (uid == null) {
      uid = RandomStringUtils.randomAlphanumeric(8);
      UIDS.set(uid);
    }
    return uid;
  }

  protected String ctx() {
    // 构造上下文信息
    final StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
    final StackTraceElement stack = stacks[3];
    StringBuilder context = new StringBuilder();
    context.append(this.scn(stack.getClassName())).append('.').append(stack.getMethodName())
        .append("-").append(stack.getLineNumber());
    return context.toString();
  }

  private String scn(String clazz) {
    final int index = clazz.lastIndexOf('.');
    if (index == -1)
      return clazz;
    return clazz.substring(index + 1);
  }

}
