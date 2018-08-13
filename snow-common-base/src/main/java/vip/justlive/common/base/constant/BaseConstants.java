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
package vip.justlive.common.base.constant;

/**
 * 基本常量
 * 
 * @author wubo
 *
 */
public class BaseConstants {

  // common

  /**
   * 验证web启动key
   */
  public static final String CHECK_WEB_KEY = "4c30c1be81144134bb9dc766ddf7f1b6";

  /**
   * 返回实体code属性字段
   */
  public static final String RESP_CODE_FIELD = "code";

  /**
   * 返回实体message属性字段
   */
  public static final String RESP_MESSAGE_FIELD = "message";

  /**
   * 返回实体success属性字段
   */
  public static final String RESP_IS_SUCCESS = "success";

  /**
   * 配置文件默认地址
   */
  public static final String CONFIG_PATH = "classpath:/config/*.properties";

  /**
   * override配置文件地址属性key
   */
  public static final String CONFIG_OVERRIDE_FILE_KEY = "config.override.file";

  /**
   * ioc扫码路径属性key
   */
  public static final String IOC_SCAN_KEY = "main.ioc.scan";

  /**
   * vertx verticle路径key
   */
  public static final String VERTICLE_PATH_KEY = "main.verticle.path";

  /**
   * 逗号分隔符
   */
  public static final String COMMA_SEPARATOR = ",";

  /**
   * 匹配所有
   */
  public static final String ANY = "*";
  /**
   * 匹配所有路径
   */
  public static final String ANY_PATH = "/*";

  /**
   * 根目录
   */
  public static final String ROOT_PATH = "/";

  /**
   * URL协议-文件
   */
  public static final String URL_PROTOCOL_FILE = "file";

  /**
   * URL协议-jar
   */
  public static final String URL_PROTOCOL_JAR = "jar";

  /**
   * URL协议-war
   */
  public static final String URL_PROTOCOL_WAR = "war";

  /**
   * URL协议-zip
   */
  public static final String URL_PROTOCOL_ZIP = "zip";

  /**
   * URL协议-WebSphere jar
   */
  public static final String URL_PROTOCOL_WSJAR = "wsjar";

  /**
   * URL协议-JBoss jar
   */
  public static final String URL_PROTOCOL_VFSZIP = "vfszip";

  /**
   * path分隔符
   */
  public static final String PATH_SEPARATOR = ROOT_PATH;

  /**
   * 协议分隔符
   */
  public static final String PROTOCOL_SEPARATOR = ":";

  /**
   * war路径分隔符
   */
  public static final String WAR_URL_SEPARATOR = "*/";

  /**
   * jar路径分隔符
   */
  public static final String JAR_URL_SEPARATOR = "!/";

  // 正则

  /**
   * Email校验正则
   */
  public static final String REGEX_EMAIL = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";

  /**
   * 身份证校验正则
   */
  public static final String REGEX_IDCARD = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}";

  /**
   * 二代身份证校验正则
   */
  public static final String REGEX_IDCARD2ND = "[1-9]\\d{16}[a-zA-Z0-9]{1}";

  /**
   * 手机校验正则
   */
  public static final String REGEX_MOBILE = "(\\+\\d+)?1[34578]\\d{9}$";

  /**
   * 电话校验正则
   */
  public static final String REGEX_PHONE = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";

  /**
   * 整数校验正则
   */
  public static final String REGEX_DIGIT = "[\\-\\+]?\\d+";

  /**
   * 空白字符校验正则
   */
  public static final String REGEX_BLANK_SPACE = "\\s+";

  /**
   * 中文字符校验正则
   */
  public static final String REGEX_CHINESE = "^[\u4E00-\u9FA5]+$";

  /**
   * 邮政编码校验正则
   */
  public static final String REGEX_POSTCODE = "[1-9]\\d{5}";

  /**
   * ipv4校验正则
   */
  public static final String REGEX_IP =
      "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";

  // mvc相关

  /**
   * csrf默认名称
   */
  public static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";

  /**
   * csrf参数名称
   */
  public static final String CSRF_PARAMETER_NAME = "_csrf";

  /**
   * csrf http Header 名称
   */
  public static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";

  private BaseConstants() {}
}
