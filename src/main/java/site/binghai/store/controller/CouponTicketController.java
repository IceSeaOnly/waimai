package site.binghai.store.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.store.config.IceConfig;
import site.binghai.store.entity.Category;
import site.binghai.store.entity.CouponTicket;
import site.binghai.store.enums.CouponTypeEnum;
import site.binghai.store.enums.PayBizEnum;
import site.binghai.store.service.CategoryService;
import site.binghai.store.service.CouponTicketService;
import site.binghai.store.tools.TimeTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by IceSea on 2018/4/27.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("/admin/coupon/")
@RestController
public class CouponTicketController extends BaseController {
    @Autowired
    private IceConfig config;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CouponTicketService couponTicketService;

    @RequestMapping("list")
    public Object list() {
        JSONObject data = newJSONObject();
        List<CouponTicket> ticketList = new ArrayList<>();
        int page = 0;
        List<CouponTicket> tmp = couponTicketService.findAll(page++, 200);
        while (tmp != null && !tmp.isEmpty()) {
            ticketList.addAll(tmp);
            tmp = couponTicketService.findAll(page++, 200);
        }

        ticketList = ticketList.stream().filter(v -> v.getRemark() == null).collect(Collectors.toList());

        JSONArray arr = newJSONArray();
        for (CouponTicket ticket : ticketList) {
            JSONObject obj = toJsonObject(ticket);
            obj.put("uuid", config.getServer() + "/user/access2Coupon?uuid=" + ticket.getUuid());
            obj.put("activityStartTime", TimeTools.format(ticket.getActivityStartTime()));
            obj.put("activityEndTime", TimeTools.format(ticket.getActivityEndTime()));
            obj.put("val", ticket.getVal() / 100.0);
            obj.put("usingType", PayBizEnum.valueOf(ticket.getAppCode()).getName());
            obj.put("typeName", CouponTypeEnum.valueOf(ticket.getCouponType()).getName());
            obj.put("discountLimit", ticket.getDiscountLimit() / 100.0);
            arr.add(obj);
        }
        data.put("list", arr);
        data.put("appCodes", getBizOptions());
        data.put("couponTypes", getAppCodeOptions());
        return success(data, "SUCCESS");
    }

    public Object getAppCodeOptions() {
        JSONArray types = newJSONArray();
        for (CouponTypeEnum pbe : CouponTypeEnum.values()) {
            JSONObject it = newJSONObject();
            it.put("label", pbe.getName());
            it.put("value", pbe.getCode());
            types.add(it);
        }
        return types;
    }

    @GetMapping("getCouponTypeMap")
    public Object getCouponTypeMap() {
        JSONObject object = newJSONObject();
        for (CouponTypeEnum type : CouponTypeEnum.values()) {
            object.put(type.getCode() + "", type.getName());
        }
        return success(object, "SUCCESS");
    }

    public Object getBizOptions() {
        JSONArray bizs = newJSONArray();
        for (PayBizEnum pbe : PayBizEnum.values()) {
            JSONObject it = newJSONObject();
            it.put("label", pbe.getName());
            it.put("value", pbe.getCode());
            bizs.add(it);
        }
        return bizs;
    }

    @RequestMapping("add")
    public Object add(@RequestBody Map map) {
        map.put("val", Double.valueOf(getDoubleValue(map, "val") * 100).intValue());
        map.put("discountLimit", Double.valueOf(getDoubleValue(map, "discountLimit") * 100).intValue());
        map.put("randomFrom", Double.valueOf(getDoubleValue(map, "randomFrom") * 100).intValue());
        map.put("randomTo", Double.valueOf(getDoubleValue(map, "randomTo") * 100).intValue());
        Long regionId = getLong(map, "regionId");
        Category category = categoryService.findById(regionId);
        map.put("regionName", category.getName());
        CouponTicket ticket = couponTicketService.newInstance(map);
        ticket.setUuid(UUID.randomUUID().toString());
        couponTicketService.save(ticket);
        return success();
    }

    @PostMapping("update")
    public Object update(@RequestBody Map map) {
        // 仅可更新余量
        Integer remaining = getInteger(map, "remaining");
        Long id = getLong(map, "id");

        CouponTicket ticket = couponTicketService.findById(id);
        if (ticket == null) return fail("FUCK U !");
        ticket.setRemaining(remaining);
        couponTicketService.update(ticket);

        return success();
    }
}
