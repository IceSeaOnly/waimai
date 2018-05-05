package site.binghai.store.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.store.config.IceConfig;
import site.binghai.store.entity.FruitTakeOut;
import site.binghai.store.entity.UnifiedOrder;
import site.binghai.store.enums.OrderStatusEnum;
import site.binghai.store.enums.PayBizEnum;
import site.binghai.store.enums.TakeOutStatusEnum;
import site.binghai.store.service.ExpressOrderService;
import site.binghai.store.service.FruitTakeOutService;
import site.binghai.store.service.UnifiedOrderService;
import site.binghai.store.service.WxService;
import site.binghai.store.tools.HttpUtils;
import site.binghai.store.tools.MD5;
import site.binghai.store.tools.TimeTools;
import site.binghai.store.tools.TplGenerator;

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
    @Autowired
    private ExpressOrderService expressOrderService;
    @Autowired
    private WxService wxService;
    @Autowired
    private IceConfig iceConfig;

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
            item.put("operations", buildOperations(order));
            list.add(item);
        }

        JSONObject data = newJSONObject();
        data.put("list", list);
        data.put("total", unifiedOrderService.countByAppCodeAndRegionIdAndStatus(payBiz, regionId, status.getCode()));
        data.put("pageSize", pageSize);
        data.put("currentPage", page);
        return success(data, "SUCCESS");
    }

    /**
     * 接单
     */
    @RequestMapping("accept")
    public Object accept(@RequestParam Long id) {
        UnifiedOrder order = unifiedOrderService.findById(id);
        logger.info("{} 已接单 {}", getAdmin(), order);
        order.setStatus(OrderStatusEnum.PROCESSING.getCode());
        unifiedOrderService.update(order);
        switch (PayBizEnum.valueOf(order.getAppCode())) {
            case FRUIT_TAKE_OUT:
                FruitTakeOut out = fruitTakeOutService.findByUnifiedId(id);
                out.setTakeOutStatus(TakeOutStatusEnum.PRODUCTING.getCode());
                fruitTakeOutService.update(out);
                break;
        }
        return success();
    }

    /**
     * 取消并退款
     */
    @RequestMapping("cancel")
    public Object cancel(@RequestParam Long id) {
        UnifiedOrder order = unifiedOrderService.findById(id);
        logger.info("{} 已取消 {}", getAdmin(), order);
        refund(id);
        order.setStatus(OrderStatusEnum.CANCELED_REFUNDED.getCode());
        unifiedOrderService.update(order);
        switch (PayBizEnum.valueOf(order.getAppCode())) {
            case FRUIT_TAKE_OUT:
                FruitTakeOut out = fruitTakeOutService.findByUnifiedId(id);
                out.setTakeOutStatus(TakeOutStatusEnum.ENDED.getCode());
                fruitTakeOutService.update(out);
                break;
        }
        return success();
    }

    /**
     * done
     */
    @RequestMapping("done")
    public Object done(@RequestParam Long id) {
        UnifiedOrder order = unifiedOrderService.findById(id);
        logger.info("{} 已完成 {}", getAdmin(), order);
        order.setStatus(OrderStatusEnum.COMPLETE.getCode());
        unifiedOrderService.update(order);
        switch (PayBizEnum.valueOf(order.getAppCode())) {
            case FRUIT_TAKE_OUT:
                FruitTakeOut out = fruitTakeOutService.findByUnifiedId(id);
                out.setTakeOutStatus(TakeOutStatusEnum.ENDED.getCode());
                fruitTakeOutService.update(out);
                break;
        }
        return success();
    }

    /**
     * refund
     */
    @RequestMapping("refund")
    public Object refund(@RequestParam Long id) {
        UnifiedOrder order = unifiedOrderService.findById(id);
        refundOpt(order);
        logger.info("{} 已退款 {}", getAdmin(), order);
        order.setStatus(OrderStatusEnum.CANCELED_REFUNDED.getCode());
        unifiedOrderService.update(order);
        switch (PayBizEnum.valueOf(order.getAppCode())) {
            case FRUIT_TAKE_OUT:
                FruitTakeOut out = fruitTakeOutService.findByUnifiedId(id);
                out.setTakeOutStatus(TakeOutStatusEnum.ENDED.getCode());
                fruitTakeOutService.update(out);
                break;
        }

        wxService.tplMessage(iceConfig.getRefundRequestAcceptedTpl(), TplGenerator.getInstance()
                .put("first", "您申请的退款已经完成")
                .put("keyword1", order.getShouldPay() / 100.0 + "元")
                .put("keyword2", order.getTitle())
                .put("keyword3", order.getOrderId())
                .put("remark", "期待您的再次光临!")
                .getAll(), order.getOpenId(), "");
        return success();
    }

    private void refundOpt(UnifiedOrder order) {
        String validate = MD5.encryption(order.getOrderId() + iceConfig.getRefundSecret() + order.getShouldPay());
        String url = iceConfig.getRefundServer() + "?out_trade_no=" + order.getOrderId() + "&refund_fee=" + order.getShouldPay() + "&validate=" + validate;
        String res = HttpUtils.sendGet(url, null);
        logger.warn("refund url:", url);
        logger.warn("refund message:{} ,order:{}", res, order);
    }

    /**
     * refuseRefund
     */
    @RequestMapping("refuseRefund")
    public Object refuseRefund(@RequestParam Long id) {
        UnifiedOrder order = unifiedOrderService.findById(id);
        logger.info("{} 已拒绝退款 {}", getAdmin(), order);
        order.setStatus(OrderStatusEnum.COMPLETE.getCode());
        unifiedOrderService.update(order);
        switch (PayBizEnum.valueOf(order.getAppCode())) {
            case FRUIT_TAKE_OUT:
                FruitTakeOut out = fruitTakeOutService.findByUnifiedId(id);
                out.setTakeOutStatus(TakeOutStatusEnum.ENDED.getCode());
                fruitTakeOutService.update(out);
                break;
        }

        wxService.tplMessage(iceConfig.getRefundRequestAcceptedTpl(), TplGenerator.getInstance()
                .put("first", "很抱歉,您申请的退款没有通过！")
                .put("keyword3", order.getShouldPay() / 100.0 + "元")
                .put("keyword4", "管理员拒绝操作")
                .put("keyword2", TimeTools.now())
                .put("keyword1", order.getOrderId())
                .put("remark", "期待您的再次光临!")
                .getAll(), order.getOpenId(), "");
        return success();
    }


    /**
     * 传入该订单的操作按钮
     */
    private JSONArray buildOperations(UnifiedOrder order) {
        JSONArray arr = newJSONArray();

        OrderStatusEnum status = OrderStatusEnum.valueOf(order.getStatus());
        switch (status) {
            case CREATED:
            case PAYING:
                break;
            case PAIED:
                arr.add(buildButtonOperation("/admin/uo/accept", "接单", true));
                arr.add(buildButtonOperation("/admin/uo/cancel", "取消", false));
                break;
            case PROCESSING:
                if(order.getAppCode().equals(PayBizEnum.EXPRESS.getCode())){
                    getSession().setAttribute("unifiedId",order.getId());
                    arr.add(buildButtonOperation("#editExpress","编辑",false));
                }
                arr.add(buildButtonOperation("/admin/uo/done", "完成", true));
            case COMPLETE:
                if(order.getAppCode().equals(PayBizEnum.EXPRESS.getCode())){
                    getSession().setAttribute("unifiedId",order.getId());
                    arr.add(buildButtonOperation("#editExpress","编辑",false));
                }
                break;
            case REFUNDING:
                arr.add(buildButtonOperation("/admin/uo/refuseRefund", "拒绝", false));
                arr.add(buildButtonOperation("/admin/uo/refund", "同意", true));
                break;
            default:
                break;

        }
        return arr;
    }

    /**
     * 创建按钮方法
     *
     * @param url     点击该按钮后回调的链接
     * @param name    按钮名
     * @param actived 是否显示为激活态
     */
    private JSONObject buildButtonOperation(String url, String name, Boolean actived) {
        JSONObject btn = newJSONObject();
        btn.put("url", url);
        btn.put("name", name);
        btn.put("active", actived);
        return btn;
    }

    /**
     * 根据不同类型的订单补充详细的订单信息
     */
    private Object getMoreOrderInfo(UnifiedOrder order) {
        PayBizEnum biz = PayBizEnum.valueOf(order.getAppCode());

        switch (biz) {
            case FRUIT_TAKE_OUT:
                return fruitTakeOutService.moreInfo(order);
            case EXPRESS:
                return expressOrderService.moreInfo(order);
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
