package site.binghai.store.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.store.entity.CouponTicket;
import site.binghai.store.enums.CouponTypeEnum;
import site.binghai.store.enums.PayBizEnum;
import site.binghai.store.service.CouponTicketService;

import java.util.Map;
import java.util.UUID;

/**
 * Created by IceSea on 2018/4/27.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("/admin/coupon/")
@RestController
public class CouponTicketController extends BaseController {
    @Autowired
    private CouponTicketService couponTicketService;

    @RequestMapping("list")
    public Object list() {
        JSONObject data = newJSONObject();
        data.put("list",couponTicketService.findAll(9999));
        data.put("appCodes", PayBizEnum.values());
        data.put("couponTypes", CouponTypeEnum.values());
        return success(couponTicketService.findAll(9999),"SUCCESS");
    }

    @RequestMapping("add")
    public Object add(@RequestBody Map map){
        CouponTicket ticket = couponTicketService.newInstance(map);
        ticket.setUuid(UUID.randomUUID().toString());
        couponTicketService.save(ticket);
        return success();
    }
}
