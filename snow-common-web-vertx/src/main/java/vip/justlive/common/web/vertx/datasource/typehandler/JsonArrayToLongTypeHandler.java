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
package vip.justlive.common.web.vertx.datasource.typehandler;

import io.vertx.core.json.JsonArray;
import vip.justlive.common.base.datasource.TypeHandler;

/**
 * Long类型转换
 * 
 * @author wubo
 *
 */
public class JsonArrayToLongTypeHandler implements TypeHandler<JsonArray, Long> {

  @Override
  public Class<JsonArray> getInType() {
    return JsonArray.class;
  }

  @Override
  public Class<Long> getOutType() {
    return Long.class;
  }

  @Override
  public Long getResult(JsonArray resultSet, int index) {
    return resultSet.getLong(index);
  }
}
