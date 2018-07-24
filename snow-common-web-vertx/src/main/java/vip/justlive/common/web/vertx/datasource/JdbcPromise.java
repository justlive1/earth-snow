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
package vip.justlive.common.web.vertx.datasource;

import java.util.ArrayList;
import java.util.List;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * jdbc约定
 * 
 * @author wubo
 *
 * @param <R> 泛型
 */
public class JdbcPromise<R> implements Handler<AsyncResult<R>> {

  protected List<Then<R>> successes = new ArrayList<>();
  protected List<Then<Throwable>> fails = new ArrayList<>();

  @Override
  public void handle(AsyncResult<R> event) {
    if (event.succeeded()) {
      R result = event.result();
      for (Then<R> then : successes) {
        then.then(result);
      }
    } else {
      for (Then<Throwable> then : fails) {
        then.then(event.cause());
      }
    }
  }

  /**
   * 成功后续处理
   *
   * @param then 处理逻辑
   * @return promise
   */
  public JdbcPromise<R> succeeded(Then<R> then) {
    successes.add(then);
    return this;
  }

  /**
   * 失败后续处理
   *
   * @param then 处理逻辑
   * @return promise
   */
  public JdbcPromise<R> failed(Then<Throwable> then) {
    fails.add(then);
    return this;
  }
}
