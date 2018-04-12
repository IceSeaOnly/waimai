package site.binghai.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.store.entity.Admin;
import site.binghai.store.service.AdminService;

import java.util.List;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/7.
 * GitHub: https://github.com/IceSeaOnly
 */
@RestController
@RequestMapping("/admin/admin/")
public class AdminController extends BaseController {
    @Autowired
    private AdminService adminService;

    @RequestMapping("list")
    public Object list() {
        List<Admin> admins = adminService.findAll(9999);
        for (Admin admin : admins) {
            admin.setPassWord(null);
        }
        return success(admins, "SUCCESS");
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Object add(@RequestBody Map map) {
        if (!getAdmin().getSuperAdmin()) {
            return fail("只有超管可以添加新的管理员!");
        }

        Admin admin = adminService.newInstance(map);
        admin.setId(null);
        admin = adminService.save(admin);
        logger.warn("{} add new admin: {}", getAdmin(), admin);
        return success(admin, "SUCCESS");
    }

    @RequestMapping("delete")
    public Object delete(@RequestParam Long id) {
        if (!getAdmin().getSuperAdmin()) {
            return fail("只有超管可以执行此操作!");
        }

        if(getAdmin().getId().equals(id)){
            return fail("自己不能删除自己!");
        }

        Admin admin = adminService.deleteIfExist(id);
        if (admin == null) {
            return fail("admin not exist!");
        }
        logger.warn("{} deleted {}", getAdmin(), admin);
        return success(admin, "SUCCESS");
    }

    /**
     * 管理更新接口暂不开放
     */
//    @RequestMapping(value = "update", method = RequestMethod.POST)
    public Object update(@RequestBody Map map) {
        Admin admin = null;
        try {
            admin = adminService.updateAndSave(getAdmin(), map);
        } catch (Exception e) {
            logger.error("{} update ADMIN {} failed!", getAdmin(), admin, e);
            return fail(e.getMessage());
        }

        return success(admin, "SUCCESS");
    }

    @RequestMapping("modifyPass")
    public Object modifyPass(@RequestBody Map map) {
        String pass = getString(map, "passwd");
        if (pass == null) {
            return fail("非法请求!");
        }
        getAdmin().setPassWord(pass);
        Admin me = adminService.update(getAdmin());
        getSession().invalidate();
        return success(me, "SUCCESS");
    }
}
