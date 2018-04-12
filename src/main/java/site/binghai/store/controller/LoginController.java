package site.binghai.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import site.binghai.store.entity.Admin;
import site.binghai.store.service.AdminService;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@Controller
@RequestMapping("login")
public class LoginController extends BaseController {
    @Autowired
    private AdminService adminService;

    /**
     * 用户前往微信身份认证
     */
    @RequestMapping("userLogin")
    public String userLogin() {
        return "redirect:";
    }

    /**
     * 管理登录
     */
    @RequestMapping(value = "adminLogin", method = RequestMethod.GET)
    public String adminLogin() {
        return "redirect:/adminDashBoard/#/login";
    }

    @RequestMapping(value = "adminLogin", method = RequestMethod.POST)
    @ResponseBody
    public Object loginAdmin(@RequestBody Map map, HttpSession session) {
        Object userName = map.get("userName");
        Object passWord = map.get("passWord");

        if (null == userName || null == passWord) {
            return fail("非法参数");
        }

        Admin admin = adminService.adminLogin(userName.toString(), passWord.toString());
        if (admin != null) {
            session.setAttribute("admin", admin);
            logger.info(String.format("ID:%d %s login success.", admin.getId(), admin.getUserName()));
            return success();
        }

        logger.error(String.format("U:%s ,P:%s login failed!", userName, passWord));
        return fail("账号或密码错误!");
    }

    @RequestMapping("getAdminName")
    @ResponseBody
    public Object getAdminName() {
        if (getAdmin() == null) {
            return fail("Not Login Yet");
        }
        return success(getAdmin().getUserName(), "SUCCESS");
    }

    @RequestMapping("adminLogout")
    public String adminLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/adminDashBoard/";
    }
}
