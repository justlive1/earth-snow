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
package vip.justlive.common.base.logger.support;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Data;
import vip.justlive.common.base.logger.Track;

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
  protected static final ThreadLocal<String> UIDS = new ThreadLocal<>();

  /**
   * 分隔符
   */
  protected static final String SEPERATOR = "-";
  protected static final String CLASS_SEPERATOR = ".";

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
   * 是否开启访问日志 - Controller
   */
  private boolean accessEnabled;

  /**
   * 是否开启服务日志 - Service
   */
  private boolean serviceEnabled;

  /**
   * 是否开启网关日志 - Gateway
   */
  private boolean gatewayEnabled;

  /**
   * 是否开启批处理日志 - Batch
   */
  private boolean batchEnabled;

  @Override
  public void request(String format, Object... args) {
    if (accessEnabled) {
      String info = String.format("REQ [%s] [%s] [%s] [%s]", uid(), req(), ctx(), format);
      access.info(info, args);
    }
  }

  @Override
  public void response(String format, Object... args) {
    if (accessEnabled) {
      String info = String.format("RES [%s] [%s] [%s] [%s]", uid(), req(), ctx(), format);
      access.info(info, args);
    }
    UIDS.remove();
  }

  @Override
  public void service(String format, Object... args) {
    if (serviceEnabled) {
      String info = String.format("SRV [%s] [%s] [%s] [%s]", uid(), req(), ctx(), format);
      service.info(info, args);
    }
  }

  @Override
  public void gateway(String format, Object... args) {
    if (gatewayEnabled) {
      String info = String.format("GTW [%s] [%s] [%s] [%s]", uid(), req(), ctx(), format);
      gateway.info(info, args);
    }
  }

  @Override
  public void batch(String format, Object... args) {
    if (batchEnabled) {
      String info = String.format("BAT [%s] [%s] [%s] [%s]", uid(), req(), ctx(), format);
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

  protected String req() {
    return SEPERATOR;
  }

  protected String ctx() {
    // 构造上下文信息
    final StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
    final StackTraceElement stack = stacks[3];
    StringBuilder context = new StringBuilder();
    context.append(this.scn(stack.getClassName())).append(CLASS_SEPERATOR)
        .append(stack.getMethodName()).append(SEPERATOR).append(stack.getLineNumber());
    return context.toString();
  }

  private String scn(String clazz) {
    final int index = clazz.lastIndexOf(CLASS_SEPERATOR);
    if (index == -1) {
      return clazz;
    }
    return clazz.substring(index + 1);
  }

}
