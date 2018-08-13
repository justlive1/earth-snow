package vip.justlive.common.base.ioc;

public class NoDepBean implements Inter{

  @Override
  public void print() {
    System.out.println("this is a non dependencies bean");
  }

}
