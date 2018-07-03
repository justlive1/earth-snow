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
package vip.justlive.common.spring.rabbit.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.extern.slf4j.Slf4j;

/**
 * hessian序列化
 * 
 * @author wubo
 *
 */
@Slf4j
public class HessianCodecFactory {

  private HessianCodecFactory() {}

  /**
   * 序列化
   * 
   * @param obj 实例对象
   * @return 字节数组
   * @throws IOException 序列化失败时抛出
   */
  public static byte[] serialize(Object obj) throws IOException {
    HessianOutput output = null;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream(1024)) {
      output = new HessianOutput(baos);
      output.startCall();
      output.writeObject(obj);
      output.completeCall();
      return baos.toByteArray();
    }
  }

  /**
   * 反序列化
   * 
   * @param in 字节数组
   * @return 对象
   * @throws IOException 反序列化失败时抛出
   */
  public static Object deSerialize(byte[] in) throws IOException {
    Object obj = null;
    HessianInput input = null;
    try (ByteArrayInputStream bais = new ByteArrayInputStream(in)) {
      input = new HessianInput(bais);
      input.startReply();
      obj = input.readObject();
      input.completeReply();
    } catch (final Throwable e) {
      log.error("Failed to decode object.", e);
    }
    return obj;
  }
}
