package vip.justlive.common.base.support;

import org.junit.Assert;
import org.junit.Test;
import vip.justlive.common.base.support.ConfigFactory;

public class ConfigFactoryTest {

  @Test
  public void test() {

    ConfigFactory.loadProperties("classpath:/config/config.properties");
    Prop prop = ConfigFactory.load(Prop.class);

    Assert.assertNotNull(prop);
    Assert.assertEquals("jack", prop.getName());
    Assert.assertEquals(new Integer(18), prop.getAge());

  }

}
