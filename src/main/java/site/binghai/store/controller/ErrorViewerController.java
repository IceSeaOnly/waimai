package site.binghai.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by IceSea on 2018/5/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("/")
@Controller
public class ErrorViewerController extends BaseController {
    @RequestMapping("error")
    public String error(ModelMap map) {
        return commonResp("系统错误", "您的参数有误，系统无法识别", "返回主页", "/user/index", map);
    }
}
