package site.binghai.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.store.config.IceConfig;
import site.binghai.store.entity.*;
import site.binghai.store.enums.OrderStatusEnum;
import site.binghai.store.enums.PayBizEnum;
import site.binghai.store.enums.TakeOutStatusEnum;
import site.binghai.store.service.*;
import site.binghai.store.tools.MD5;
import site.binghai.store.tools.TimeTools;
import site.binghai.store.tools.TplGenerator;

/**
 * Created by IceSea on 2018/4/30.
 * GitHub: https://github.com/IceSeaOnly
 */
@RestController
@RequestMapping("/")
public class AfterPay extends BaseController {
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private WxService wxService;
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private FruitTakeOutService fruitTakeOutService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ExpressOrderService expressOrderService;

    /**
     * orderKey = orderId
     * 'validate' => md5($openid)
     */
    @RequestMapping("afterPay")
    public Object afterPay(@RequestParam String orderKey, @RequestParam String validate) {
        logger.info("after pay:{}", orderKey);
        UnifiedOrder order = unifiedOrderService.findByOrderId(orderKey);
        if (order == null) {
            return fail("order not exist:" + orderKey);
        }
        if (!MD5.encryption(order.getOpenId()).equals(validate)) {
            return fail("validate failed!");
        }
        if (OrderStatusEnum.valueOf(order.getStatus()) == OrderStatusEnum.PAIED) {
            return fail("state not correct");
        }

        wxService.tplMessage(iceConfig.getPaySuccessTpl(), TplGenerator.getInstance()
                .put("first", String.format("%s订单支付成功", order.getTitle()))
                .put("keyword1", order.getShouldPay() / 100.0 + "元")
                .put("keyword2", order.getOrderId())
                .put("remark", "感谢您的光临!")
                .getAll(), order.getOpenId(), null);

        switch (PayBizEnum.valueOf(order.getAppCode())) {
            case EXPRESS:
                ExpressOrder expressOrder = expressOrderService.findByUnifiedId(order.getId());
                expressOrder.setHasPay(true);
                expressOrderService.update(expressOrder);
                managerService.findByRegionId(order.getRegionId())
                        .forEach(v -> payExpressSuccess(v, order));
                break;
            case FRUIT_TAKE_OUT:
                FruitTakeOut out = fruitTakeOutService.findByUnifiedId(order.getId());
                out.setTakeOutStatus(TakeOutStatusEnum.PRODUCTING.getCode());
                fruitTakeOutService.update(out);
                managerService.findByRegionId(order.getRegionId())
                        .forEach(v -> payFruitOrderSuccess(v, order));
                break;
        }

        return success();
    }

    private void payFruitOrderSuccess(Manager manager, UnifiedOrder order) {
        FruitTakeOut out = fruitTakeOutService.findByUnifiedId(order.getId());
        UserAddress address = addressService.findById(out.getAddressId());

        wxService.tplMessage(iceConfig.getNewOrderNoticeTpl(), TplGenerator.getInstance()
                .put("first", "外卖订单提醒!用户已支付，请尽快处理!")
                .put("keyword1", order.getTitle())
                .put("keyword2", TimeTools.now())
                .put("keyword3", order.getShouldPay() / 100.0 + "元")
                .put("keyword4", address.getUserName() + ":" + address.getUserPhone())
                .put("keyword5", address.getAddressDetail())
                .put("remark", "点击查看详情")
                .getAll(), manager.getOpenId(), iceConfig.getServer() + "/user/fruitOrderDetail?unifiedId=" + order.getId() + "&openid=" + manager.getOpenId());
    }

    private void payExpressSuccess(Manager manager, UnifiedOrder order) {
        wxService.tplMessage(iceConfig.getNewOrderNoticeTpl(), TplGenerator.getInstance()
                .put("first", "新" + order.getTitle() + "订单到达!用户已支付，请尽快处理!")
                .put("keyword1", order.getTitle())
                .put("keyword2", TimeTools.now())
                .put("keyword3", "Id" + order.getUserId())
                .put("keyword4", order.getUserName())
                .put("keyword5", "已支付")
                .put("remark", "点击查看详情")
                .getAll(), manager.getOpenId(), iceConfig.getServer() + "/user/orderDetail?unifiedId=" + order.getId() + "&openid=" + manager.getOpenId());
    }
}
