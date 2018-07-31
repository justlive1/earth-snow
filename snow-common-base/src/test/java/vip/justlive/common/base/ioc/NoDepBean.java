package vip.justlive.common.base.ioc;

import vip.justlive.common.base.annotation.Singleton;

@Singleton("NoDepBean")
public class NoDepBean {

  public void print() {
    System.out.println("this is a non dependencies bean");
  }

}
