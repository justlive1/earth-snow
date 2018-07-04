package vip.justlive.common.web.vertx;

import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import io.vertx.core.Vertx;

/**
 * Test
 * 
 * @author wubo
 *
 */
public class ApplicationTest {

  @Test
  public void testRoute() throws InterruptedException {

    CountDownLatch latch = new CountDownLatch(1);

    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(VerticleDemo.class.getName(), r -> {
      vertx.createHttpClient().get(8080, "localhost", "/demo/test/1000?request=path")
          .putHeader("header", "true").handler(res -> {
            res.bodyHandler(System.out::println);
          }).exceptionHandler(System.out::println).endHandler(s -> latch.countDown()).end();

    });

    latch.await();

  }

}
