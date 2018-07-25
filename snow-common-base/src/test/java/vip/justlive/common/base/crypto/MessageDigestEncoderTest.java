package vip.justlive.common.base.crypto;

import static org.junit.Assert.*;
import org.junit.Test;

public class MessageDigestEncoderTest {

  @Test
  public void test() {

    MessageDigestEncoder encoder = new MessageDigestEncoder("MD5");

    String source = "111111";
    assertTrue(encoder.match(source, "96e79218965eb72c92a549dd5a330112"));

    encoder.setUseSalt(true);
    String raw = encoder.encode(source);
    assertTrue(encoder.match(source, raw));

    DelegateEncoder e = new DelegateEncoder("MD5");
    assertTrue(e.match(source, "{MD5}96e79218965eb72c92a549dd5a330112"));
  }

}
