package site.binghai.store.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import site.binghai.store.entity.Admin;
import site.binghai.store.entity.User;
import site.binghai.store.tools.BaseBean;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by IceSea on 2018/4/5.
 * GitHub: https://github.com/IceSeaOnly
 */
public class BaseController extends BaseBean {
    /**
     * 从thread local获取网络上下文
     */
    public HttpServletRequest getServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes;
        if (requestAttributes instanceof ServletRequestAttributes) {
            servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    public Admin getAdmin() {
        Object obj = getServletRequest().getSession().getAttribute("admin");
        return obj == null ? null : (Admin) obj;
    }

    public User getUser() {
        return (User) getServletRequest().getSession().getAttribute("user");
    }

    public JSONObject fail(String err) {
        JSONObject object = new JSONObject();
        object.put("status", "FAIL");
        object.put("msg", err);
        return object;
    }

    public Object jsoupFail(String err, String callback) {
        if (StringUtils.isEmpty(callback)) {
            return fail(err);
        }
        return callback + "(" + fail(err).toJSONString() + ")";
    }

    public JSONObject success() {
        JSONObject object = new JSONObject();
        object.put("status", "SUCCESS");
        return object;
    }

    public JSONObject success(String msg) {
        JSONObject object = new JSONObject();
        object.put("status", "SUCCESS");
        object.put("msg", msg);
        return object;
    }

    public Object jsoupSuccess(Object data, String msg, String callBack) {
        if (StringUtils.isEmpty(callBack)) {
            return success(data, msg);
        }
        callBack = callBack == null ? "" : callBack;
        return callBack + "(" + success(data, msg).toJSONString() + ")";
    }

    public JSONObject success(Object data, String msg) {
        JSONObject object = new JSONObject();
        object.put("status", "SUCCESS");
        object.put("data", data);
        object.put("msg", msg);
        return object;
    }
}
