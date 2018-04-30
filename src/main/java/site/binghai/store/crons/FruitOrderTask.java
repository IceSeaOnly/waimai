package site.binghai.store.crons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.store.config.IceConfig;
import site.binghai.store.entity.FruitTakeOut;
import site.binghai.store.entity.UnifiedOrder;
import site.binghai.store.enums.OrderStatusEnum;
import site.binghai.store.enums.TakeOutStatusEnum;
import site.binghai.store.service.FruitTakeOutService;
import site.binghai.store.service.UnifiedOrderService;
import site.binghai.store.service.UserCouponService;
import site.binghai.store.service.WxService;
import site.binghai.store.tools.BaseBean;
import site.binghai.store.tools.TimeTools;
import site.binghai.store.tools.TplGenerator;

import java.util.List;

/**
 * Created by IceSea on 2018/4/24.
 * GitHub: https://github.com/IceSeaOnly
 */
@Component
public class FruitOrderTask extends BaseBean {
    @Autowired
    private FruitTakeOutService fruitTakeOutService;
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
        logger.info("FruitOrderTask start");

        List<FruitTakeOut> ls = fruitTakeOutService.findByState(TakeOutStatusEnum.WAITING_PAY);
        if (ls == null) return;
        for (FruitTakeOut l : ls) {
            if (TimeTools.currentTS() - l.getCreated() > 30 * 60000) {
                l.setTakeOutStatus(TakeOutStatusEnum.ENDED.getCode());
                fruitTakeOutService.update(l);

                UnifiedOrder unifiedOrder = unifiedOrderService.findById(l.getUnifiedOrderId());
                unifiedOrder.setStatus(OrderStatusEnum.OUTOFDATE.getCode());
                unifiedOrderService.update(unifiedOrder);

                userCouponService.rollBackCoupon(unifiedOrder);

                wxService.tplMessage(iceConfig.getOrderCanceledNotice(), TplGenerator.getInstance()
                        .put("first","您的外卖订单已取消")
                        .put("orderProductPrice",unifiedOrder.getShouldPay()/100.0+"元")
                        .put("orderProductName",unifiedOrder.getTitle())
                        .put("orderAddress","请进入详情查看")
                        .put("orderName",unifiedOrder.getOrderId())
                        .put("remark","欢迎您的再次光临")
                        .getAll(),unifiedOrder.getOpenId(),iceConfig.getServer()+"/user/confirmOrder?unifiedId="+unifiedOrder.getId());

                logger.info("order {},unifiedOrder {} has been cancelled because of out of date.", l.getId(), l.getUnifiedOrderId());
            }
        }
    }
}
