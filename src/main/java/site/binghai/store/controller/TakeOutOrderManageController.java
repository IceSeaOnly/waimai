package site.binghai.store.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.store.entity.FruitTakeOut;
import site.binghai.store.service.FruitTakeOutService;

import java.util.List;

/**
 * Created by IceSea on 2018/4/8.
 * GitHub: https://github.com/IceSeaOnly
 */
@RestController
@RequestMapping("/admin/order/")
public class TakeOutOrderManageController extends BaseController {
    @Autowired
    private FruitTakeOutService fruitTakeOutService;

    /**
     * 外卖订单列表
     */
    @RequestMapping("list")
    public Object list(Integer page, Integer pageSize) {
        if (page == null || page < 0 || page > 100) page = 10;
        if (pageSize == null || pageSize < 0) pageSize = 1;

        List<FruitTakeOut> ls = fruitTakeOutService.list(page, pageSize);
        long total = fruitTakeOutService.count();
        JSONObject data = newJSONObject();
        data.put("list", ls);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        return success(data, "SUCCESS");
    }
}
