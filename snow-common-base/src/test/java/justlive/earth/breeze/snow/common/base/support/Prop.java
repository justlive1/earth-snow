package justlive.earth.breeze.snow.common.base.support;

import justlive.earth.breeze.snow.common.base.annotation.Value;
import lombok.Data;

@Data
public class Prop {

  @Value("${fc.name}")
  private String name;

  @Value("${fc.age}")
  private Integer age;
  
}
