package site.binghai.store.inters;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by IceSea on 2018/4/5.
 * GitHub: https://github.com/IceSeaOnly
 * 用户登录拦截器
 */
public class UserInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (session != null) {
            if (session.getAttribute("user") != null) {
                return true;
            }
        }
        response.sendRedirect("/login/userLogin");
        return false;
    }
}
