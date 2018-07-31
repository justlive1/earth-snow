package vip.justlive.common.base.ioc;

import vip.justlive.common.base.annotation.Inject;
import vip.justlive.common.base.annotation.Singleton;

@Singleton
public class DepBean {

  private final NoDepBean noDepBean;

  @Inject
  public DepBean(NoDepBean noDepBean) {
    this.noDepBean = noDepBean;
  }

  public void print() {
    System.out.println("this bean has dependency of NoDepBean");
    noDepBean.print();
  }

}
