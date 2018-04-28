package site.binghai.store.crons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.store.entity.ExpressOrder;
import site.binghai.store.service.ExpressOrderService;
import site.binghai.store.tools.BaseBean;
import site.binghai.store.tools.TimeTools;

import java.util.List;

/**
 * Created by IceSea on 2018/4/28.
 * GitHub: https://github.com/IceSeaOnly
 */
@Component
public class ExpressOrderTask extends BaseBean {
    @Autowired
    private ExpressOrderService expressOrderService;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void start() {
        logger.info("ExpressOrderTask start.");
        List<ExpressOrder> ls = expressOrderService.findByPayState(Boolean.FALSE);
        ls.forEach(v->{
            if(TimeTools.currentTS() - v.getCreated() > 60000*30){
                v.setCanceled(true);
                expressOrderService.update(v);
            }
        });
    }
}
