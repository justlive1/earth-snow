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

import java.time.OffsetDateTime;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import io.vertx.core.json.JsonArray;
import vip.justlive.common.base.datasource.TypeHandler;
import vip.justlive.common.base.exception.Exceptions;

/**
 * 日期类型转换
 * 
 * @author wubo
 *
 */
public class JsonArrayToDateTypeHandler implements TypeHandler<JsonArray, Date> {

  @Override
  public Class<JsonArray> getInType() {
    return JsonArray.class;
  }

  @Override
  public Class<Date> getOutType() {
    return Date.class;
  }

  @Override
  public Date getResult(JsonArray resultSet, int index) {
    String date = resultSet.getString(index);
    try {
      return Date.from(OffsetDateTime.parse(date).toInstant());
    } catch (Exception e) {
      try {
        return DateUtils.parseDate(date, "yyyy-MM-dd HH:mm:ss");
      } catch (Exception e1) {
        throw Exceptions.wrap(e1);
      }
    }
  }

}
