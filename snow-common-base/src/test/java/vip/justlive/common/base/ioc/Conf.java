package vip.justlive.common.base.ioc;

import vip.justlive.common.base.annotation.Configuration;
import vip.justlive.common.base.annotation.Singleton;

@Configuration
public class Conf {

  @Singleton
  Inter noDepBean() {
    return new NoDepBean();
  }

}
