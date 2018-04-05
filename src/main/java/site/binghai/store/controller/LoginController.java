package site.binghai.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by IceSea on 2018/4/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@Controller
@RequestMapping("login")
public class LoginController {
    /**
     * 用户前往微信身份认证
     * */
    @RequestMapping("userLogin")
    public String userLogin(){
        return "redirect:";
    }

    /**
     * 管理登录
     * */
    @RequestMapping("adminUser")
    public String adminUser(){
        return "redirect:";
    }
}
