package site.binghai.store.controller.user;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.store.controller.BaseController;
import site.binghai.store.entity.Coupon;
import site.binghai.store.entity.CouponTicket;
import site.binghai.store.entity.UnifiedOrder;
import site.binghai.store.enums.CouponStatusEnum;
import site.binghai.store.enums.OrderStatusEnum;
import site.binghai.store.enums.PayBizEnum;
import site.binghai.store.service.CouponTicketService;
import site.binghai.store.service.UnifiedOrderService;
import site.binghai.store.service.UserCouponService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IceSea on 2018/4/24.
 * GitHub: https://github.com/IceSeaOnly
 */
@Controller
@RequestMapping("user")
public class UserCouponController extends BaseController {
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private UserCouponService userCouponService;
    @Autowired
    private CouponTicketService couponTicketService;

    /**
     * 优惠券列表页
     */
    @RequestMapping("couponList")
    public String couponList(Integer exp, Long unifiedId, ModelMap map) {
        List<Coupon> couponList = userCouponService.listByUser(getUser());
        final UnifiedOrder order = unifiedOrderService.findById(unifiedId);

        if (unifiedId != null) {
            if (order == null || !order.getUserId().equals(getUser().getId())) {
                return commonResp("输入有误!", "请勿拼接参数", "返回主页", "/user/index", map);
            }
        }

        couponList = couponList.stream()
                .filter(v -> expFilter(v, exp))
                .filter(v -> order == null || v.getRegionId().equals(order.getRegionId()))
                .filter(v -> order == null || v.getAppCode().equals(order.getAppCode()))
                .collect(Collectors.toList());

        if (unifiedId != null && CollectionUtils.isEmpty(couponList)) {
            String url = null;
            PayBizEnum biz = PayBizEnum.valueOf(order.getAppCode());
            switch (biz) {
                case FRUIT_TAKE_OUT:
                    url = "/user/confirmOrder?unifiedId=";
                    break;
                case EXPRESS:
                    url = "/user/confirmExpressOrder?unifiedId=";
                    break;
            }
            return commonResp("无可用优惠券", "该订单没有任何可用优惠券", "好的", url + unifiedId, map);
        }

        userCouponService.couponInfo(couponList);

        map.put("unifiedId", unifiedId);
        map.put("hideTab", unifiedId != null);
        map.put("coupons", couponList);
        map.put("exp", exp == null);
        return "userCouponList";
    }

    private boolean expFilter(Coupon v, Integer exp) {
        return exp == null ?
                (v.getCouponStatus() == CouponStatusEnum.AVAILABLE.getCode())
                : (v.getCouponStatus() != CouponStatusEnum.AVAILABLE.getCode());
    }

    /**
     * 优惠券与订单绑定
     */
    @RequestMapping("set2Order")
    public String set2Order(@RequestParam Long unifiedId, @RequestParam Long cpId, ModelMap map) {
        UnifiedOrder order = unifiedOrderService.findById(unifiedId);

        if (order == null || !order.getUserId().equals(getUser().getId())) {
            return commonResp("输入有误", "请勿拼接参数", "返回主页", "/user/index", map);
        }

        try {
            userCouponService.bindOrder(cpId, order);
        } catch (Exception e) {
            logger.error("{} bind coupon {} failed.", order, cpId, e);
            return commonResp("发生错误", e.getMessage(), "返回主页", "/user/index", map);
        }

        unifiedOrderService.update(order);
        String url = null;
        PayBizEnum biz = PayBizEnum.valueOf(order.getAppCode());
        switch (biz) {
            case FRUIT_TAKE_OUT:
                url = "/user/confirmOrder?unifiedId=";
                break;
            case EXPRESS:
                url = "/user/confirmExpressOrder?unifiedId=";
                break;
        }
        return "redirect:" + url + order.getId();
    }

    /**
     * 用户领券
     */
    @RequestMapping("access2Coupon")
    public String access2Coupon(@RequestParam String uuid, ModelMap map) {
        CouponTicket ticket = couponTicketService.findByUUID(uuid);
        if (ticket == null) {
            return commonResp("优惠券错误", "请勿拼接参数", "返回主页", "/user/index", map);
        }
        if (ticket.getRemaining() <= 0) {
            return commonResp("来晚了", "优惠券已被领完!", "返回主页", "/user/index", map);
        }
        Coupon coupon = userCouponService.findByTicketId(ticket.getId());
        if (coupon != null) {
            return commonResp("不要太贪心哦", "你已经领过该优惠券了!", "返回主页", "/user/index", map);
        }
        coupon = new Coupon();
        coupon.setAppCode(ticket.getAppCode());
        coupon.setCouponStatus(CouponStatusEnum.AVAILABLE.getCode());
        coupon.setCouponId(ticket.getId());
        coupon.setOpenId(getUser().getOpenId());
        coupon.setUserId(getUser().getId());
        coupon.setRegionId(ticket.getRegionId());
        coupon.setUserName(getUser().getUserName());
        coupon.setOutOfDateTs(ticket.getActivityEndTime());

        userCouponService.save(coupon);
        ticket.setRemaining(ticket.getRemaining() - 1);
        couponTicketService.update(ticket);

        return commonResp("领取成功", "恭喜你获得一张优惠券!" + ticket.getDescribe(), "好的", "/user/index", map);
    }
}
