package site.binghai.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.store.service.CityService;

import java.util.Map;

/**
 * Created by IceSea on 2018/5/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@RestController
@RequestMapping("/admin/city/")
public class CityController extends BaseController {
    @Autowired
    private CityService cityService;

    @RequestMapping("list")
    public Object list() {
        return success(cityService.findAll(9999), "SUCCESS");
    }


    @RequestMapping("update")
    public Object update(@RequestBody Map map) {
        try {
            cityService.updateAndSave(getAdmin(), map);
        } catch (Exception e) {
            return fail("更新失败!" + e.getMessage());
        }
        return success();
    }
}
