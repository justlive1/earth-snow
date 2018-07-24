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
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;

/**
 * models约定
 * 
 * @author wubo
 *
 * @param <T> 泛型
 */
public class ModelsPromise<T> extends JdbcPromise<ResultSet> {

  private final Repository<T> repository;

  ModelsPromise(Repository<T> repository) {
    this.repository = repository;
  }

  protected List<Then<List<T>>> succes = new ArrayList<>();

  @Override
  public void handle(AsyncResult<ResultSet> event) {
    if (event.succeeded()) {
      List<JsonArray> list = event.result().getResults();
      List<T> results = new ArrayList<>();
      if (list != null && !list.isEmpty()) {
        for (JsonArray jsonArray : list) {
          results.add(RepositoryHelper.convert(jsonArray, repository.tableInfo, repository.clazz));
        }
      }
      for (Then<List<T>> then : succes) {
        then.then(results);
      }
    } else {
      for (Then<Throwable> then : fails) {
        then.then(event.cause());
      }
    }
  }

  public ModelsPromise<T> then(Then<List<T>> then) {
    succes.add(then);
    return this;
  }

}
