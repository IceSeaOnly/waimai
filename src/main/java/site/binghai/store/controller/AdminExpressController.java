package site.binghai.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.store.service.ExpressOrderService;

import java.util.List;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/27.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("/admin/express/")
@RestController
public class AdminExpressController extends BaseController {
    @Autowired
    private ExpressOrderService expressOrderService;

    /**
     * 仅列出支付成功的
     */
    @RequestMapping("list")
    public Object list(@RequestParam Integer page, @RequestParam Integer pageSize) {
        List ls = expressOrderService.findAllPaiedDescById(page, pageSize);
        return success(ls, "SUCCESS");
    }

    @RequestMapping("update")
    public Object update(@RequestBody Map map) {
        try {
            expressOrderService.updateAndSave(getAdmin(), map);
        } catch (Exception e) {
            logger.error("{} update {} failed!", getAdmin(), map, e);
            return fail("更新失败!");
        }

        return success();
    }
}
