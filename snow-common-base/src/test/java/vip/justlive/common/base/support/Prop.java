package vip.justlive.common.base.support;

import lombok.Data;
import vip.justlive.common.base.annotation.Value;

@Data
public class Prop {

  @Value("${fc.name}")
  private String name;

  @Value("${fc.age}")
  private Integer age;
  
}
