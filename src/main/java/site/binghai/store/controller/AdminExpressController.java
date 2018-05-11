package site.binghai.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import site.binghai.store.entity.ExpressOrder;
import site.binghai.store.service.ExpressOrderService;

import java.util.List;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/27.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("/admin/express/")
@Controller
public class AdminExpressController extends BaseController {
    @Autowired
    private ExpressOrderService expressOrderService;

    /**
     * 仅列出支付成功的
     */
    @ResponseBody
    @RequestMapping("list")
    public Object list(@RequestParam Integer page, @RequestParam Integer pageSize) {
        List ls = expressOrderService.findAllPaiedDescById(page, pageSize);
        return success(ls, "SUCCESS");
    }

    @RequestMapping("update")
    @ResponseBody
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


    @GetMapping("edit")
    public String redirect(@RequestParam Long id){
        getSession().setAttribute("exoId",id);
        return "redirect:/#/editExpress";
    }
}
