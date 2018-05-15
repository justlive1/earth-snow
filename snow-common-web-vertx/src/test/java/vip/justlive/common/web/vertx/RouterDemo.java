package vip.justlive.common.web.vertx;

import vip.justlive.common.web.vertx.annotation.VertxHeaderParam;
import vip.justlive.common.web.vertx.annotation.VertxPathParam;
import vip.justlive.common.web.vertx.annotation.VertxRequestBody;
import vip.justlive.common.web.vertx.annotation.VertxRequestParam;
import vip.justlive.common.web.vertx.annotation.VertxRoute;
import vip.justlive.common.web.vertx.annotation.VertxRouteMapping;

@VertxRoute("/demo")
public class RouterDemo {

  @VertxRouteMapping(value = "/test/:path")
  public String test1(@VertxRequestParam("request") String request,
      @VertxHeaderParam("header") Boolean header, @VertxPathParam("path") Long path) {
    return String.format("{%s},{%s},{%s}", request, header, path);
  }

  @VertxRouteMapping(value = "/test")
  public String test2(@VertxRequestBody Body body) {
    return String.format("%s", body);
  }
}
