package site.binghai.store.crons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.store.service.UserCouponService;
import site.binghai.store.tools.BaseBean;

/**
 * Created by IceSea on 2018/4/24.
 * GitHub: https://github.com/IceSeaOnly
 */
@Component
public class CouponTask extends BaseBean {
    @Autowired
    private UserCouponService userCouponService;

    @Scheduled(cron = "0 * * * * ?")
    public void start() {
        logger.info("CouponTask start");
        userCouponService.outOfDate();
    }
}
