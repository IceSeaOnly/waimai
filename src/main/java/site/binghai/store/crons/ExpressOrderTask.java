package site.binghai.store.crons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.store.config.IceConfig;
import site.binghai.store.entity.ExpressOrder;
import site.binghai.store.entity.UnifiedOrder;
import site.binghai.store.enums.OrderStatusEnum;
import site.binghai.store.service.ExpressOrderService;
import site.binghai.store.service.UnifiedOrderService;
import site.binghai.store.service.UserCouponService;
import site.binghai.store.service.WxService;
import site.binghai.store.tools.BaseBean;
import site.binghai.store.tools.TimeTools;
import site.binghai.store.tools.TplGenerator;

import java.util.List;

/**
 * Created by IceSea on 2018/4/28.
 * GitHub: https://github.com/IceSeaOnly
 */
@Component
public class ExpressOrderTask extends BaseBean {
    @Autowired
    private ExpressOrderService expressOrderService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private UserCouponService userCouponService;
    @Autowired
    private WxService wxService;
    @Autowired
    private IceConfig iceConfig;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void start() {
        logger.info("ExpressOrderTask start.");
        List<ExpressOrder> ls = expressOrderService.findByPayStateAndCanceled(Boolean.FALSE, Boolean.FALSE);
        ls.forEach(v -> {
            if (TimeTools.currentTS() - v.getCreated() > 60000 * 30) {
                v.setCanceled(true);
                expressOrderService.update(v);

                UnifiedOrder unifiedOrder = unifiedOrderService.findById(v.getUnifiedId());
                unifiedOrder.setStatus(OrderStatusEnum.OUTOFDATE.getCode());
                unifiedOrderService.update(unifiedOrder);

                userCouponService.rollBackCoupon(unifiedOrder);

                wxService.tplMessage(iceConfig.getOrderCanceledNotice(), TplGenerator.getInstance()
                        .put("first", "您的" + unifiedOrder.getTitle() + "订单已取消")
                        .put("orderProductPrice", unifiedOrder.getShouldPay() / 100.0 + "元")
                        .put("orderProductName", unifiedOrder.getTitle())
                        .put("orderAddress", "请进入详情查看")
                        .put("orderName", unifiedOrder.getOrderId())
                        .put("remark", "欢迎您的再次光临")
                        .getAll(), unifiedOrder.getOpenId(), iceConfig.getServer() + "/user/confirmExpressOrder?unifiedId=" + unifiedOrder.getId());
            }
        });
    }
}
