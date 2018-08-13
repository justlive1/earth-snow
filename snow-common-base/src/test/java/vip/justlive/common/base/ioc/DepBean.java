package vip.justlive.common.base.ioc;

import vip.justlive.common.base.annotation.Inject;
import vip.justlive.common.base.annotation.Singleton;

@Singleton("xxx")
public class DepBean implements Inter{

  private final NoDepBean noDepBean;

  @Inject
  public DepBean(NoDepBean noDepBean) {
    this.noDepBean = noDepBean;
  }

  @Override
  public void print() {
    System.out.println("this bean has dependency of NoDepBean");
    noDepBean.print();
  }

}
