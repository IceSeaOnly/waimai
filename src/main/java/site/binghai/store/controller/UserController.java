package site.binghai.store.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.store.entity.User;
import site.binghai.store.service.UserService;

import java.util.List;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/8.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("/admin/user/")
@RestController
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @RequestMapping("list")
    public Object list(Integer page, Integer pageSize) {
        if (page == null || page < 0 || page > 100) page = 10;
        if (pageSize == null || pageSize < 0) pageSize = 1;

        List<User> ls = userService.findAll(page, pageSize);
        long total = userService.count();
        JSONObject data = newJSONObject();
        data.put("list", ls);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        return success(data, "SUCCESS");
    }

    @RequestMapping(value = "update",method = RequestMethod.POST)
    public Object update(@RequestBody Map map) {
        User user = null;
        try {
            user = userService.updateAndSave(getAdmin(), map);
        } catch (Exception e) {
            logger.error("{} update user {} error!", getAdmin(), map, e);
            return fail(e.getMessage());
        }

        return success(user, "SUCCESS");
    }

    @RequestMapping("delete")
    public Object delete(@RequestParam Long id) {
        User user = userService.deleteIfExist(id);
        if (user == null) {
            return fail("User not exist!");
        }

        logger.warn("{} deleted user {}", getAdmin(), user);
        return success(user, "SUCCESS");
    }
}
