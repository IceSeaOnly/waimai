package site.binghai.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.store.entity.Manager;
import site.binghai.store.entity.User;
import site.binghai.store.service.ManagerService;
import site.binghai.store.service.UserService;

import java.util.Map;

/**
 * Created by IceSea on 2018/5/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("/admin/manager/")
@RestController
public class ManagerController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private ManagerService managerService;

    @GetMapping("list")
    public Object list() {
        return success(managerService.findAll(9999), "SUCCESS");
    }

    @GetMapping("delete")
    public Object delete(@RequestParam Long id) {
        Manager m = managerService.deleteIfExist(id);
        logger.warn("admin {} deleted manager {}", getAdmin(), m);
        return success();
    }

    @GetMapping("add")
    public Object add(@RequestBody Map map) {
        Long userId = getLong(map, "userId");
        Long regionId = getLong(map, "regionId");
        String name = getString(map,"name");
        User user = userService.findById(userId);
        if (user != null) {
            Manager manager = new Manager();
            manager.setRegionId(regionId);
            manager.setUserId(userId);
            manager.setOpenId(user.getOpenId());
            manager.setName(name);
            managerService.save(manager);
        }
        return success();
    }
}
