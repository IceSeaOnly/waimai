package site.binghai.store.controller.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.store.controller.BaseController;
import site.binghai.store.entity.*;
import site.binghai.store.enums.OrderStatusEnum;
import site.binghai.store.enums.PayBizEnum;
import site.binghai.store.enums.TakeOutStatusEnum;
import site.binghai.store.service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/24.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("user")
@Controller
public class UserOrderController extends BaseController {
    @Autowired
    private TradeItemService tradeItemService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private FruitTakeOutService fruitTakeOutService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AddressService addressService;

    /**
     * 前往购物页面
     */
    @RequestMapping("cart")
    public String cart(ModelMap map) {
        User user = getUser();
        if (user.getRegionId() == null) {
            return commonResp("欢迎光临!", "为了提供更好的服务，请先完善信息", "点我完善信息", "/user/memberInfo", map);
        }
        List<Category> menus = categoryService.listAllByFid(user.getRegionId());
        if (CollectionUtils.isEmpty(menus)) {
            return commonResp("空空如也", "店铺没有上任何商品哦~ 等等再来吧~", "好的", "/user/index", map);
        }

        List itemSet = emptyList();
        List<Long> itemIds = emptyList();
        for (Category menu : menus) {
            List<TradeItem> items = tradeItemService.findByCategoryId(menu.getId());
            items.forEach(v -> itemIds.add(v.getId()));
            itemSet.add(items == null ? emptyList() : items);
        }

        map.put("menus", menus);
        map.put("itemSet", itemSet);
        map.put("itemIds", itemIds);
        return "userCart";
    }


    /**
     * 提交商城订单
     */
    @RequestMapping(value = "submitOrder", method = RequestMethod.POST)
    public String submitOrder(ModelMap map) {
        Map<Long, Integer> dic = new HashMap<>();

        getServletRequest().getParameterMap().forEach((k, v) -> {
            if (k.startsWith("item_")) {
                int count = Integer.valueOf(v[0]);
                if (count > 0) {
                    dic.put(Long.valueOf(k.split("_")[1]), count);
                }
            }
        });

        int sum = 0;

        List<TradeItem> items = tradeItemService.findByIds(new ArrayList<>(dic.keySet()));
        JSONArray data = newJSONArray();
        for (TradeItem v : items) {
            if (v == null) continue;
            if (v.getRemains() < 1 || !v.getOnLine()) {
                return commonResp("下单失败...", "非常抱歉,您点单中的" + v.getName() + "已经售罄/线下，请重新点单。", "好的", "/user/cart", map);
            }
            if (v.getRemains() < dic.get(v.getId())) {
                return commonResp("下单失败...", "非常抱歉,您点单中的" + v.getName() + "备货不足，请重新点单。", "好的", "/user/cart", map);
            }
            v.setRemains(v.getRemains() - dic.get(v.getId()));
            v.setSaleCount(v.getSaleCount() + dic.get(v.getId()));
            tradeItemService.update(v);

            sum += v.getPrice() * dic.get(v.getId());
            JSONObject item = newJSONObject();
            item.put("name", v.getName());
            item.put("img", v.getImg());
            item.put("num", dic.get(v.getId()));
            item.put("price", v.getDoublePrice());

            data.add(item);
        }

        if (sum <= 0) {
            return commonResp("Sorry", "您的点单没产生任何费用,请重新点单。", "重新点单", "/user/cart", map);
        }

        User user = getUser();
        UnifiedOrder order = unifiedOrderService.newOrder(PayBizEnum.FRUIT_TAKE_OUT, user, "水果外卖订单", sum);

        FruitTakeOut fruitTakeOut = new FruitTakeOut();
        UserAddress address = addressService.getUserAddress(user.getId());
        fruitTakeOut.setUserId(user.getId());
        fruitTakeOut.setAddressId(address == null ? null : address.getId());
        fruitTakeOut.setTakeOutStatus(TakeOutStatusEnum.WAITING_PAY.getCode());
        fruitTakeOut.setUnifiedOrderId(order.getId());
        fruitTakeOut.setTradeItemJson(data.toJSONString());

        fruitTakeOut = fruitTakeOutService.save(fruitTakeOut);

        return "redirect:confirmOrder?unifiedId=" + fruitTakeOut.getUnifiedOrderId();
    }

    /**
     * 订单详情页
     */
    @RequestMapping("confirmOrder")
    public String confirmOrder(@RequestParam Long unifiedId, ModelMap map) {
        FruitTakeOut fruitTakeOut = fruitTakeOutService.findByUnifiedId(unifiedId);

        if (fruitTakeOut == null) {
            return commonResp("参数有误", "订单不存在", "返回主页", "/user/index", map);
        }
        UnifiedOrder unifiedOrder = unifiedOrderService.findById(fruitTakeOut.getUnifiedOrderId());
        if (unifiedOrder.getOrderId().equals(getUser().getId())) {
            return commonResp("参数有误", "订单所属有误", "返回主页", "/user/index", map);
        }


        UserAddress address = addressService.getUserAddress(getUser().getId());

        map.put("order", fruitTakeOut);
        map.put("uorder", unifiedOrder);
        map.put("addressMiss", address == null ? true : false);
        map.put("address", address);
        map.put("couponInfo", unifiedOrder.getCouponId() == null ? "未使用优惠券" : "已使用优惠券");
        map.put("conponPrice", (unifiedOrder.getOriginalPrice() - unifiedOrder.getShouldPay()) / 100.0); // 优惠金额

        return "userConfirmOrder";
    }

    /**
     * 管理员访问路径
     * */
    @RequestMapping("fruitOrderDetail")
    public String fruitOrderDetail(@RequestParam Long unifiedId, @RequestParam String openid, ModelMap map) {
        if (!openid.equals(getUser().getOpenId())) {
            return commonResp("无权查看", "对不起,您无权查看此信息", "好的", "/user/index", map);
        }

        FruitTakeOut fruitTakeOut = fruitTakeOutService.findByUnifiedId(unifiedId);

        if (fruitTakeOut == null) {
            return commonResp("参数有误", "订单不存在", "返回主页", "/user/index", map);
        }
        UnifiedOrder unifiedOrder = unifiedOrderService.findById(fruitTakeOut.getUnifiedOrderId());
        UserAddress address = addressService.getUserAddress(getUser().getId());
        map.put("order", fruitTakeOut);
        map.put("uorder", unifiedOrder);
        map.put("addressMiss", address == null ? true : false);
        map.put("address", address);
        map.put("couponInfo", unifiedOrder.getCouponId() == null ? "未使用优惠券" : "已使用优惠券");
        map.put("conponPrice", (unifiedOrder.getOriginalPrice() - unifiedOrder.getShouldPay()) / 100.0); // 优惠金额

        return "userConfirmOrder";
    }

    /**
     * 订单列表页
     */
    @RequestMapping("orderList")
    public String orderList(ModelMap map) {
        List<FruitTakeOut> orders = fruitTakeOutService.listByUserAndBiz(getUser());
        if (!CollectionUtils.isEmpty(orders)) {
            map.put("orders", orders);
        }
        return "userOrderList";
    }

    @RequestMapping("goToPay")
    public String goToPay(@RequestParam Long unifiedId, ModelMap map) {
        return commonResp("不可支付", "支付系统对接中...", "返回主页", "/user/index", map);
    }

    @RequestMapping("cancelOrder")
    public String cancelOrder(@RequestParam Long unifiedId, ModelMap map) {
        UnifiedOrder order = unifiedOrderService.findById(unifiedId);
        if (order.getStatus() < 2) {
            return commonResp("自动取消", "未支付订单半小时后自动取消并退还优惠券", "返回", "/user/orderList", map);
        }

        if (order.getStatus() > 3) {
            return commonResp("不可取消", "订单已完成或正在取消流程中", "返回", "/user/orderList", map);
        }

        order.setStatus(OrderStatusEnum.REFUNDING.getCode());
        unifiedOrderService.update(order);
        if (order.getAppCode().equals(PayBizEnum.FRUIT_TAKE_OUT.getCode())) {
            FruitTakeOut out = fruitTakeOutService.findByUnifiedId(order.getId());
            out.setTakeOutStatus(TakeOutStatusEnum.MANAGAER_DOING.getCode());
            fruitTakeOutService.update(out);
        }
        //todo 退款操作
        return commonResp("退款流程已发起", "退款流程已发起，待管理员审核，取消后将退款、退还优惠券", "返回", "/user/orderList", map);
    }
}
