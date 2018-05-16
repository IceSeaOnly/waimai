package site.binghai.store.controller.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.store.config.IceConfig;
import site.binghai.store.controller.BaseController;
import site.binghai.store.entity.*;
import site.binghai.store.enums.OrderStatusEnum;
import site.binghai.store.enums.PayBizEnum;
import site.binghai.store.enums.TakeOutStatusEnum;
import site.binghai.store.service.*;
import site.binghai.store.tools.TplGenerator;

import java.util.*;

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
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private WxService wxService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private RegionConfigService regionConfigService;
    @Autowired
    private ExpressOrderService expressOrderService;
    @Autowired
    private PrintService printService;

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

        List itemSet = emptyList();
        List<Long> itemIds = emptyList();
        for (Category menu : menus) {
            List<TradeItem> items = tradeItemService.findByCategoryId(menu.getId());
            items.forEach(v -> itemIds.add(v.getId()));
            itemSet.add(items == null ? emptyList() : items);
        }

        if (CollectionUtils.isEmpty(itemIds)) {
            return commonResp("空空如也", "店铺没有上任何商品哦~ 等等再来吧~", "好的", "/user/index", map);
        }

        map.put("menus", menus);
        map.put("regionConfig", regionConfigService.findByRegionId(getUser().getRegionId()));
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

        map.put("isOwner", true);
        map.put("order", fruitTakeOut);
        map.put("uorder", unifiedOrder);
        map.put("addressMiss", address == null ? true : false);
        map.put("address", address);
        map.put("couponInfo", unifiedOrder.getCouponId() == null ? "未使用优惠券" : "已使用优惠券");
        map.put("conponPrice", (unifiedOrder.getOriginalPrice() - unifiedOrder.getShouldPay()) / 100.0); // 优惠金额

        return "userConfirmOrder";
    }

    @RequestMapping("confirmReceived")
    public String confirmReceived(@RequestParam Long unifiedId, ModelMap map) {
        FruitTakeOut fruitTakeOut = fruitTakeOutService.findByUnifiedId(unifiedId);

        if (fruitTakeOut == null) {
            return commonResp("参数有误", "订单不存在", "返回主页", "/user/index", map);
        }

        UnifiedOrder unifiedOrder = unifiedOrderService.findById(fruitTakeOut.getUnifiedOrderId());
        if (unifiedOrder.getOrderId().equals(getUser().getId())) {
            return commonResp("参数有误", "订单所属有误", "返回主页", "/user/index", map);
        }

        unifiedOrder.setStatus(OrderStatusEnum.COMPLETE.getCode());
        fruitTakeOut.setTakeOutStatus(TakeOutStatusEnum.USER_RECEIVED_CONFIRMED.getCode());

        unifiedOrderService.update(unifiedOrder);
        fruitTakeOutService.update(fruitTakeOut);
        map.put("isOwner", true);

        return "redirect:/user/orderList";
    }

    /**
     * 管理员访问路径
     */
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
        map.put("isOwner", false);
        String printKey = UUID.randomUUID().toString();
        getSession().setAttribute("printKey", printKey);
        map.put("printKey", printKey);

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
        UnifiedOrder order = unifiedOrderService.findById(unifiedId);
        if (order == null) {
            return commonResp("不可支付", "参数错误!", "返回主页", "/user/index", map);
        }
        return "redirect:" + iceConfig.getPayServer() + "?orderid=" + order.getOrderId();
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

        refundRequest(order);
        return commonResp("退款流程已发起", "退款流程已发起，待管理员审核，取消后将退款、退还优惠券", "返回", "/user/orderList", map);
    }

    private void refundRequest(UnifiedOrder order) {
        managerService.findByRegionId(order.getRegionId())
                .forEach(v ->
                        wxService.tplMessage(iceConfig.getRefuntRequestNoticeTpl(), TplGenerator.getInstance()
                                .put("first", order.getTitle() + "订单申请退款，请尽快处理!")
                                .put("keyword1", order.getShouldPay() / 100.0 + "元")
                                .put("keyword2", order.getTitle())
                                .put("keyword3", order.getOrderId())
                                .put("remark", "申请已提交，请操作拒绝或通过！")
                                .getAll(), v.getOpenId(), "")

                );
    }

    @GetMapping("printOrder")
    @ResponseBody
    public Object printOrder(@RequestParam Long unifiedId, @RequestParam String printKey) {
        Object key = getSession().getAttribute("printKey");
        if (key == null || !printKey.equals(key)) {
            return fail("printKey wrong!");
        }
        UnifiedOrder order = unifiedOrderService.findById(unifiedId);
        switch (PayBizEnum.valueOf(order.getAppCode())) {
            case EXPRESS:
                printExpressOrder(order);
                break;
            case FRUIT_TAKE_OUT:
                printFruitOrder(order);
                break;
        }

        return success();
    }

    private void printFruitOrder(UnifiedOrder order) {
        FruitTakeOut out = fruitTakeOutService.findByUnifiedId(order.getId());
        PrintData data = PrintData.getInstance()
                .CenterBold("果然鲜水果外卖订单").breakLine()
                .text("下单时间:" + order.getCreatedTime()).breakLine()
                .text(String.format("%-6s %-3s %-3s %-3s", "商品名称", "单价", "数量", "金额")).breakLine()
                .text("------------------------").breakLine();

        JSONArray arr = out.getItemJSONObject();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            int num = obj.getInteger("num");
            double price = obj.getDouble("price");
            data.text(String.format("%-6s %3s x %3s = %.2f", obj.getString("name"), price, num, price * num)).breakLine();
        }

        UserAddress address = addressService.findById(out.getAddressId());
        data.text("------------------------").breakLine()
                .text("合计:" + order.shouldPayDouble()).breakLine()
                .Bold("配送地点:" + address.getAddressHead() + address.getAddressDetail()).breakLine()
                .Bold("电话：" + address.getUserPhone() + "  " + address.getUserPhone()).breakLine();

        data.CenterBold(order.orderState().getName());
        printService.print(data,regionConfigService.findByRegionId(order.getRegionId()));
    }

    private void printExpressOrder(UnifiedOrder uorder) {
        ExpressOrder order = expressOrderService.findByUnifiedId(uorder.getId());
        int type = order.getType();
        PrintData data = PrintData.getInstance()
                .CenterBold("果然鲜 " + (type == 0 ? "寄快递订单" : "取快递订单")).breakLine()
                .text("下单时间:" + order.getCreatedTime()).breakLine()
                .text("----------------").breakLine();
        UserAddress address = addressService.getUserAddress(uorder.getUserId());
        if (type == 0) {
            data.text("寄件人:" + address.getUserName()).breakLine()
                    .text("寄件地址:"+address.getAddressHead()+address.getAddressDetail()).breakLine()
                    .text(String.format("寄件人手机:%s", address.getUserPhone())).breakLine()
                    .text("收件人:" + order.getTo()).breakLine()
                    .text(String.format("收件人手机:%s", order.getToPhone())).breakLine()
                    .text("收件地址:" + order.getToWhere()).breakLine()
                    .text("预约时间:" + order.getBookPeriod()).breakLine()
                    .text("内容物:" + order.getWhatIs()).breakLine()
                    .text("身份证号:" + order.getPersonalId()).breakLine()
                    .text("快递名:" + order.getExName()).breakLine()
                    .text("快递单号:" + order.getExNo()).breakLine();
        } else {
            data.text("收件人:" + order.getFrom()).breakLine()
                    .text(String.format("收件手机:%s", order.getToPhone())).breakLine()
                    .text("短信内容:" + order.getSms()).breakLine()
                    .text("配送地址:" + order.getToWhere()).breakLine()
                    .text("预约时间:" + order.getBookPeriod()).breakLine();
        }
        data.CenterBold(String.format("费用: %.2f",uorder.getShouldPay()/100.0)).breakLine();
        data.CenterBold(uorder.orderState().getName());
        printService.print(data,regionConfigService.findByRegionId(uorder.getRegionId()));
    }


}
