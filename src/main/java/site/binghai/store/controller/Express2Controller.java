package site.binghai.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.store.entity.ExpressOrder;
import site.binghai.store.service.ExpressOrderService;

import java.util.Map;

/**
 * Created by IceSea on 2018/5/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@RestController
@RequestMapping("/admin/exp/")
public class Express2Controller extends BaseController {
    @Autowired
    private ExpressOrderService expressOrderService;

    @GetMapping("findBy")
    public Object findBy() {
        Long id = (Long) getSession().getAttribute("exoId");
        return success(expressOrderService.findByUnifiedId(id), "SUCCESS");
    }

    @PostMapping("update")
    public Object update(@RequestBody Map map) {
        try {
            Long id = getLong(map,"id");
            String exName = getString(map,"exName");
            String exNo = getString(map,"exNo");

            ExpressOrder order = expressOrderService.findByUnifiedId(id);
            if(order != null){
                order.setExName(exName);
                order.setExNo(exNo);
                expressOrderService.update(order);
            }
        } catch (Exception e) {
            logger.error("{} update {} failed!", getAdmin(), map, e);
            return fail("更新失败!");
        }

        return success();
    }
}
