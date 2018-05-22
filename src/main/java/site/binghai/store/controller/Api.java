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
    public Object expressBarCode(@RequestParam Long passCode, @RequestParam Long exId,@RequestParam String barCode) {
        ExpressOrder order = expressOrderService.findByUnifiedId(exId);
        if(order != null && order.getCreated().equals(passCode)){
            order.setExName("天天快递");
            order.setExNo(barCode);
            expressOrderService.update(order);
        }
        return success("录入成功，返回微信");
    }
}
