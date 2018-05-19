package site.binghai.store.controller.user;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import site.binghai.store.config.IceConfig;
import site.binghai.store.controller.BaseController;
import site.binghai.store.entity.User;
import site.binghai.store.service.UserService;
import site.binghai.store.service.WxService;
import site.binghai.store.tools.MD5;
import site.binghai.store.tools.UserTool;

/**
 * Created by IceSea on 2018/4/23.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("/login/")
@Controller
public class UserLoginController extends BaseController {
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private UserService userService;
    @Autowired
    private WxService wxService;

    /**
     * 用户前往微信身份认证
     */
    @RequestMapping("userLogin")
    public String userLogin(String openId, String validate, ModelMap map) {
        if (StringUtils.isEmpty(openId) || StringUtils.isEmpty(validate)) {
            return "redirect:" + iceConfig.getWmLogin();
        }

        // 验证身份
        if (iceConfig.isEnableOpenIdValidate()
                && !MD5.encryption(iceConfig.getValidateSeed() + openId).equals(validate)) {
            return commonResp("非法登录", "", "重新登录", "/login/userLogin", map);
        }

        User instance = userService.getByOpenId(openId);
        if (instance == null) {
            JSONObject info = wxService.getUserInfo(openId);
            instance = new User();
            instance.setAvatar(info.getString("headimgurl"));
            instance.setIntegral(0);
            instance.setOpenId(openId);
            instance.setUserName(info.getString("nickname"));
            instance = userService.save(instance);
        }
        getSession().setAttribute("user", instance);

        if(UserTool.isInfoMiss(instance)){
            return commonResp("欢迎!", "为了提供更好的服务，请先完善信息", "点我完善信息", "/user/memberInfo", map);
        }

        Object backUrl = getSession().getAttribute("backUrl");
        if(backUrl != null) getSession().removeAttribute("backUrl");
        return backUrl == null ? "redirect:/user/index" : "redirect:" + backUrl;
    }
}
