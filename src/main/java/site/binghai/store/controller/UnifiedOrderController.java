package site.binghai.store.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.store.enums.PayBizEnum;
import site.binghai.store.service.UnifiedOrderService;


/**
 * Created by IceSea on 2018/4/12.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("/admin/uo/")
@RestController
public class UnifiedOrderController extends BaseController {
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @RequestMapping("counts")
    public Object counts() {
        JSONArray arr = newJSONArray();

        for (PayBizEnum pb : PayBizEnum.values()) {
            JSONObject obj = newJSONObject();
            obj.put("text", pb.getName());
            obj.put("count", unifiedOrderService.countByCode(pb));
            obj.put("type", "payType_" + pb.getCode());
            obj.put("subCategories", commonSubItem());
            arr.add(obj);
        }

        return success(arr, "SUCCESS");
    }

    private JSONArray commonSubItem() {
        JSONArray array = new JSONArray();
        array.add(buildItem("待接单", "wtk"));
        array.add(buildItem("待配送", "ws"));
        array.add(buildItem("待确认完成", "wcc"));
        array.add(buildItem("已完成", "cc"));
        return array;
    }

    public JSONObject buildItem(String text, String id) {
        JSONObject obj = newJSONObject();
        obj.put("text", text);
        obj.put("id", id);
        return obj;
    }
}
