package site.binghai.store.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.store.entity.UnifiedOrder;
import site.binghai.store.enums.OrderStatusEnum;
import site.binghai.store.enums.PayBizEnum;
import site.binghai.store.service.FruitTakeOutService;
import site.binghai.store.service.UnifiedOrderService;

import java.util.List;


/**
 * Created by IceSea on 2018/4/12.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("/admin/uo/")
@RestController
public class UnifiedOrderController extends BaseController {
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private FruitTakeOutService fruitTakeOutService;

    /**
     * currentCategory: {@OrderStatusEnum}
     * currentTab ： 见下方 counts方法的type数据 payType_...
     * regionId: 即超一级类目
     */
    @RequestMapping("list")
    public Object list(@RequestParam Integer page,
                       @RequestParam Integer pageSize,
                       @RequestParam Long regionId,
                       @RequestParam(name = "currentCategory") Integer orderStatus,
                       @RequestParam String currentTab
    ) {
        page--;
        String biz = currentTab.split("_")[1];
        PayBizEnum payBiz = PayBizEnum.valueOf(Integer.valueOf(biz));
        OrderStatusEnum status = OrderStatusEnum.valueOf(orderStatus);
        List<UnifiedOrder> orders = unifiedOrderService.list(payBiz, status, page, pageSize, regionId);

        JSONArray list = newJSONArray();

        for (UnifiedOrder order : orders) {
            JSONObject item = toJsonObject(order);
            item.put("orders", getMoreOrderInfo(order));
            list.add(item);
        }

        JSONObject data = newJSONObject();
        data.put("list",list);
        data.put("total",unifiedOrderService.countByAppCodeAndRegionIdAndStatus(payBiz,regionId,status.getCode()));
        data.put("pageSize",pageSize);
        data.put("currentPage",page);
        return success(data, "SUCCESS");
    }

    /**
     * 根据不同类型的订单补充详细的订单信息
     * */
    private Object getMoreOrderInfo(UnifiedOrder order) {
        PayBizEnum biz = PayBizEnum.valueOf(order.getAppCode());

        switch (biz){
            case FRUIT_TAKE_OUT:
                return fruitTakeOutService.moreInfo(order);
        }
        return null;
    }

    @RequestMapping("counts")
    public Object counts(@RequestParam Long regionId) {
        JSONArray arr = newJSONArray();

        for (PayBizEnum pb : PayBizEnum.values()) {
            JSONObject obj = newJSONObject();
            obj.put("text", pb.getName());
            obj.put("count", unifiedOrderService.countByCodeAndRegionId(pb, regionId));
            obj.put("type", "payType_" + pb.getCode());
            obj.put("subCategories", commonSubItem());
            arr.add(obj);
        }

        return success(arr, "SUCCESS");
    }

    private JSONArray commonSubItem() {
        JSONArray array = new JSONArray();
        for (OrderStatusEnum ose : OrderStatusEnum.values()) {
            array.add(buildItem(ose.getName(), ose.getCode() + ""));
        }
        return array;
    }

    public JSONObject buildItem(String text, String id) {
        JSONObject obj = newJSONObject();
        obj.put("text", text);
        obj.put("id", id);
        return obj;
    }
}
