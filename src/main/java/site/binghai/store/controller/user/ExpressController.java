package site.binghai.store.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.store.controller.BaseController;
import site.binghai.store.entity.ExpressOrder;
import site.binghai.store.entity.RegionConfig;
import site.binghai.store.entity.UnifiedOrder;
import site.binghai.store.enums.PayBizEnum;
import site.binghai.store.service.ExpressOrderService;
import site.binghai.store.service.RegionConfigService;
import site.binghai.store.service.UnifiedOrderService;

import java.util.Arrays;

/**
 * Created by IceSea on 2018/4/27.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("user")
@Controller
public class ExpressController extends BaseController {
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private ExpressOrderService expressOrderService;
    @Autowired
    private RegionConfigService regionConfigService;

    @RequestMapping("exIndex")
    public String index(ModelMap map) {
        map.put("regionConfig",regionConfigService.findByRegionId(getUser().getRegionId()));
        map.put("orders", expressOrderService.findAllByUserId(getUser()));
        return "expressBiz";
    }

    @PostMapping("newDelivery")
    public String newDelivery(@RequestParam String to,
                              @RequestParam String toPhone,
                              @RequestParam String from,
                              @RequestParam String fromPhone,
                              @RequestParam Double fee,
                              @RequestParam String toWhere,
                              @RequestParam String whatIs,
                              ModelMap map
    ) {
        if (!noEmptyString(Arrays.asList(to, toPhone, from, fromPhone, toWhere))) {
            return commonResp("输入有误", "输入不正确，请确认输入完整", "好的", "/user/exIndex", map);
        }

        if (fee <= 0) {
            return commonResp("价格有误", "请输入正确的价格", "好的", "/user/exIndex", map);
        }

        UnifiedOrder unifiedOrder = unifiedOrderService.newOrder(PayBizEnum.EXPRESS, getUser(), "寄快递", Double.valueOf(fee * 100).intValue());

        ExpressOrder expressOrder = new ExpressOrder();
        expressOrder.setUserName(getUser().getUserName());
        expressOrder.setUserId(getUser().getId());
        expressOrder.setHasPay(false);
        expressOrder.setCanceled(false);
        expressOrder.setUnifiedId(unifiedOrder.getId());
        expressOrder.setTo(to);
        expressOrder.setToPhone(toPhone);
        expressOrder.setFrom(from);
        expressOrder.setFromPhone(fromPhone);
        expressOrder.setType(0);
        expressOrder.setToWhere(toWhere);
        expressOrder.setWhatIs(whatIs);

        expressOrder = expressOrderService.save(expressOrder);

        return "redirect:/user/confirmExpressOrder?unifiedId=" + expressOrder.getUnifiedId();
    }

    @PostMapping("newFetchThing")
    public String newFetchThing(@RequestParam String from,
                                @RequestParam String fromPhone,
                                @RequestParam String sms,
                                ModelMap map) {
        if (!noEmptyString(Arrays.asList(from, fromPhone, sms))) {
            return commonResp("输入有误", "输入不正确，请确认输入完整", "好的", "/user/exIndex", map);
        }
        RegionConfig config = regionConfigService.findByRegionId(getUser().getRegionId());
        UnifiedOrder unifiedOrder = unifiedOrderService.newOrder(PayBizEnum.EXPRESS, getUser(), "取快递", (config == null || config.getFetchFee() == null) ? 500 : config.getFetchFee());

        ExpressOrder expressOrder = new ExpressOrder();
        expressOrder.setUserName(getUser().getUserName());
        expressOrder.setUserId(getUser().getId());
        expressOrder.setHasPay(false);
        expressOrder.setCanceled(false);
        expressOrder.setUnifiedId(unifiedOrder.getId());
        expressOrder.setTo(from);
        expressOrder.setToPhone(fromPhone);
        expressOrder.setFrom(from);
        expressOrder.setFromPhone(fromPhone);
        expressOrder.setType(1);
        expressOrder.setSms(sms);

        expressOrder = expressOrderService.save(expressOrder);
        return "redirect:/user/confirmExpressOrder?unifiedId=" + expressOrder.getUnifiedId();
    }

    @GetMapping("confirmExpressOrder")
    public String confirmExpressOrder(@RequestParam Long unifiedId, ModelMap map) {
        ExpressOrder order = expressOrderService.findByUnifiedId(unifiedId);
        if (order == null || !order.getUserId().equals(getUser().getId())) {
            return commonResp("非法参数", "非法参数", "好的", "/user/Index", map);
        }
        UnifiedOrder unifiedOrder = unifiedOrderService.findById(order.getUnifiedId());
        int type = order.getType();
        StringBuilder sb = new StringBuilder();

        map.put("title", type == 0 ? "寄快递" : "取快递");
        if (type == 0) {
            sb.append("寄件人:" + order.getFrom() + "</br>");
            sb.append("寄件人手机:" + order.getFromPhone() + "</br>");
            sb.append("收件人:" + order.getFrom() + "</br>");
            sb.append("寄件人手机:" + order.getFromPhone() + "</br>");
            sb.append("寄件地址:" + order.getToWhere() + "</br>");
            sb.append("内容物:" + order.getWhatIs() + "</br>");
        } else {
            sb.append("收件人:" + order.getFrom() + "</br>");
            sb.append("电话:" + order.getFromPhone() + "</br>");
            sb.append("短信内容:" + order.getSms() + "</br>");
        }

        map.put("uorder", unifiedOrder);
        map.put("order", order);
        map.put("detail", sb.toString());
        return "confirmExpressOrder";
    }

    /**
     * 管理员访问路径
     * */
    @GetMapping("orderDetail")
    public String unifiedOrderDetail(@RequestParam Long unifiedId, @RequestParam String openid, ModelMap map) {
        if (!openid.equals(getUser().getOpenId())) {
            return commonResp("无权查看", "对不起,您无权查看此信息", "好的", "/user/index", map);
        }

        ExpressOrder order = expressOrderService.findByUnifiedId(unifiedId);
        if (order == null) {
            return commonResp("非法参数", "非法参数", "好的", "/user/Index", map);
        }
        UnifiedOrder unifiedOrder = unifiedOrderService.findById(order.getUnifiedId());
        int type = order.getType();
        StringBuilder sb = new StringBuilder();

        map.put("title", type == 0 ? "寄快递" : "取快递");
        if (type == 0) {
            sb.append("寄件人:" + order.getFrom() + "</br>");
            sb.append("寄件人手机:" + order.getFromPhone() + "</br>");
            sb.append("收件人:" + order.getFrom() + "</br>");
            sb.append("寄件人手机:" + order.getFromPhone() + "</br>");
            sb.append("寄件地址:" + order.getToWhere() + "</br>");
            sb.append("内容物:" + order.getWhatIs() + "</br>");
        } else {
            sb.append("收件人:" + order.getFrom() + "</br>");
            sb.append("电话:" + order.getFromPhone() + "</br>");
            sb.append("短信内容:" + order.getSms() + "</br>");
        }

        map.put("uorder", unifiedOrder);
        map.put("order", order);
        map.put("detail", sb.toString());
        return "confirmExpressOrder";
    }
}
