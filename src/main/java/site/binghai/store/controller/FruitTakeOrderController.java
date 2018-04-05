package site.binghai.store.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.store.entity.User;

/**
 * Created by IceSea on 2018/4/5.
 * GitHub: https://github.com/IceSeaOnly
 * 水果外卖订单
 */
@RestController
@RequestMapping("user")
public class FruitTakeOrderController extends BaseController {
    @RequestMapping(value = "newTakeOrder", method = RequestMethod.POST)
    public String newOrder(@RequestParam String addressHead,
                           @RequestParam String addressDetail) {
        User user = getUser();

        return "";
    }
}
