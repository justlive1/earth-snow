package vip.justlive.common.base.util;

import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import vip.justlive.common.base.util.ExpiringMap.CleanPolicy;
import vip.justlive.common.base.util.ExpiringMap.ExpiringPolicy;

/**
 * @author wubo
 */
public class ExpiringMapTest {

  @Test
  public void test1() throws Exception {

    ExpiringMap<String, Integer> expiringMap = ExpiringMap.<String, Integer>builder()
        // 默认失效时间 1s
        .expiration(1, TimeUnit.SECONDS)
        // 累积4次
        .accumulateThreshold(4).build();

    String key = "key";
    expiringMap.put(key, 1);

    Assert.assertNotNull(expiringMap.get(key));

    TimeUnit.SECONDS.sleep(2);

    Assert.assertNull(expiringMap.get(key));

    expiringMap.get(key);
    expiringMap.get(key);
    expiringMap.get(key);

    // 等待清理线程执行完毕
    TimeUnit.SECONDS.sleep(1);

    Assert.assertEquals(0, expiringMap.realSize());

  }

  @Test
  public void test2() throws Exception {

    ExpiringMap<String, Integer> expiringMap = ExpiringMap.<String, Integer>builder()
        // 默认失效时间 2s
        .expiration(2, TimeUnit.SECONDS)
        // 访问刷新
        .expiringPolicy(ExpiringPolicy.ACCESSED)
        // 累积
        .accumulateThreshold(4).build();

    String key = "key";
    String k = "k";
    expiringMap.put(key, 1);
    expiringMap.put(k, 1);

    Assert.assertNotNull(expiringMap.get(key));
    Assert.assertNotNull(expiringMap.get(k));

    TimeUnit.SECONDS.sleep(1);

    Assert.assertNotNull(expiringMap.get(key));

    TimeUnit.SECONDS.sleep(1);

    Assert.assertNotNull(expiringMap.get(key));

    TimeUnit.SECONDS.sleep(1);

    Assert.assertNotNull(expiringMap.get(key));
    Assert.assertNull(expiringMap.get(k));

  }

  @Test
  public void test3() throws Exception {

    ExpiringMap<String, Integer> expiringMap = ExpiringMap.<String, Integer>builder()
        // 默认失效时间 2s
        .expiration(2, TimeUnit.SECONDS)
        // 定时清理任务
        .cleanPolicy(CleanPolicy.SCHEDULE)
        // 定时任务间隔
        .scheduleDelay(3)
        // 累积
        .accumulateThreshold(4).build();

    String key = "key";
    expiringMap.put(key, 1);

    Assert.assertNotNull(expiringMap.get(key));

    TimeUnit.MILLISECONDS.sleep(2100);

    Assert.assertNull(expiringMap.get(key));
    Assert.assertEquals(1, expiringMap.realSize());

    TimeUnit.SECONDS.sleep(2);

    Assert.assertEquals(0, expiringMap.realSize());

  }

  @Test
  public void test4() throws Exception {

    ExpiringMap<String, Integer> expiringMap = ExpiringMap.<String, Integer>builder()
        // 默认失效时间 1s
        .expiration(1, TimeUnit.SECONDS)
        // 累积4次
        .accumulateThreshold(4).build();

    String key = "key";
    String k = "k";
    expiringMap.put(key, 1, 2, TimeUnit.SECONDS);
    expiringMap.put(k, 1);

    Assert.assertNotNull(expiringMap.get(key));
    Assert.assertNotNull(expiringMap.get(k));

    TimeUnit.MILLISECONDS.sleep(1100);

    Assert.assertNull(expiringMap.get(k));
    Assert.assertNotNull(expiringMap.get(key));

    TimeUnit.MILLISECONDS.sleep(1100);

    Assert.assertNull(expiringMap.get(key));

  }

}
