package justlive.earth.breeze.snow.common.web.logger;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import justlive.earth.breeze.snow.common.base.logger.support.TrackImpl;

/**
 * web 系统日志工具
 * 
 * @author wubo
 *
 */
public class WebTrack extends TrackImpl {

  private static final String UID_IN_COOKIE = "u_=";

  private HttpServletRequest request() {
    ServletRequestAttributes attrs =
        (ServletRequestAttributes) (RequestContextHolder.getRequestAttributes());
    if (attrs == null) {
      return null;
    }
    return attrs.getRequest();
  }

  @Override
  protected String req() {
    final HttpServletRequest req = request();
    if (req == null) {
      return super.req();
    }
    final String query = req.getQueryString();
    final String uri = req.getRequestURI();
    if (query == null) {
      return uri;
    }
    return uri + "?" + query;
  }

  @Override
  protected String uid() {
    String uid = UIDS.get();
    if (uid != null) {
      return uid;
    }

    final HttpServletRequest request = request();
    if (request == null) {
      return super.uid();
    }
    // 从Cookie中寻找是否有uid信息
    final String cookie = request.getHeader("Cookie");

    int index = -1;
    if (cookie != null) {
      index = cookie.indexOf(UID_IN_COOKIE);
    }
    // 没有定义uid则使用sessionId作为uid
    if (cookie == null || index < 0) {
      uid = request.getSession(true).getId();
    } else {
      // 从Cookie中取出uid信息
      index = index + UID_IN_COOKIE.length();
      int indexEnd = cookie.indexOf(';', index);
      if (indexEnd < 0) {
        // uid cookie在字符串尾
        uid = cookie.substring(index);
      } else {
        uid = cookie.substring(index, indexEnd);
      }
    }
    UIDS.set(uid);
    return uid;
  }
}
