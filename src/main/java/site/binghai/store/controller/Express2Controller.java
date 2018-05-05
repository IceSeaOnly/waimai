package site.binghai.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public Object findBy(@RequestParam Long id) {
        return success(expressOrderService.findById(id), "SUCCESS");
    }

    @PostMapping("update")
    public Object update(@RequestBody Map map) {
        try {
            expressOrderService.updateAndSave(getAdmin(), map);
        } catch (Exception e) {
            return fail(e.getMessage());
        }
        return success();
    }
}
