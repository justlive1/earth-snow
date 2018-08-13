package vip.justlive.common.base.ioc;

import org.junit.Assert;
import org.junit.Test;

public class IocTest {

  @Test
  public void test() {

    Ioc.install();

    Assert.assertNotNull(BeanStore.getBean(Inter.class));

  }

}
