package site.binghai.store.tools;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/3/5.
 */
public class UrlUtil {
    public static String getFullUrl(HttpServletRequest request){
        String strBackUrl = "http://" + request.getServerName() //服务器地址
                + ":"
                + request.getServerPort()           //端口号
                + request.getContextPath()      //项目名称
                + request.getServletPath()      //请求页面或其他地址
                + "?" + (request.getQueryString()); //参数
        return strBackUrl;
    }
}
