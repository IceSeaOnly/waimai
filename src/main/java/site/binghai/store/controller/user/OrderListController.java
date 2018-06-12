package site.binghai.store.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.store.controller.BaseController;
import site.binghai.store.service.ExpressOrderService;
import site.binghai.store.tools.MD5;

@Controller
@RequestMapping("user")
public class OrderListController extends BaseController {
    @Autowired
    private ExpressOrderService expressOrderService;

    @RequestMapping("adminExpressOrderList")
    public String adminExpressOrderList(@RequestParam String validate, @RequestParam Integer type, ModelMap map) {
        if (!MD5.encryption(getUser().getOpenId()).equals(validate)) {
            return commonResp("非法访问", "此链接不可分享", "返回首页", "/user/index", map);
        }

        map.put("type", type);
        map.put("ATab", type == 0?"active":"");
        map.put("BTab", type == 1?"active":"");
        map.put("CTab", type > 1?"active":"");
        map.put("openid",getUser().getOpenId());

        map.put("validate", validate);
        if (type == 0) {
            map.put("list", expressOrderService.findAllUnConfirmed());
        } else if (type == 1) {
            map.put("list", expressOrderService.findAllConfirmedButNotFilled());
        } else {
            map.put("list", expressOrderService.findAllFilled());
        }

        return "orderList";
    }
}
