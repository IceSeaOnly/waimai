package site.binghai.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.store.entity.ExpressOrder;
import site.binghai.store.service.ExpressOrderService;

/**
 * Created by IceSea on 2018/5/22.
 * GitHub: https://github.com/IceSeaOnly
 */
@RestController
@RequestMapping("api")
public class Api extends BaseController {
    @Autowired
    private ExpressOrderService expressOrderService;

    @GetMapping("expressBarCode")
    public Object expressBarCode(@RequestParam Long passCode, @RequestParam Long exId, String expName, @RequestParam String barCode, @RequestParam String callback) {
        ExpressOrder order = expressOrderService.findById(exId);
        if (order != null && order.getCreated().equals(passCode)) {
            order.setExName(expName == null ? "天天快递" : expName);
            order.setExNo(barCode);
            expressOrderService.update(order);
        } else {
            return jsoupFail("参数有误", callback);
        }
        return jsoupSuccess(null, "录入成功，返回微信", callback);
    }
}
