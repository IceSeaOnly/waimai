package site.binghai.store.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import site.binghai.store.def.UnifiedOrderMethods;
import site.binghai.store.entity.ExpressOrder;
import site.binghai.store.entity.UnifiedOrder;
import site.binghai.store.entity.User;
import site.binghai.store.service.dao.ExpressDao;

import java.util.List;

/**
 * Created by IceSea on 2018/4/27.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class ExpressOrderService extends BaseService<ExpressOrder> implements UnifiedOrderMethods {
    @Autowired
    private ExpressDao expressDao;

    public List<ExpressOrder> findAllPaiedDescById(Integer page, Integer pageSize) {
        return expressDao.findAllByHasPayOrderByIdDesc(true, new PageRequest(page, pageSize));
    }

    public List<ExpressOrder> findAllByUserId(User user) {
        return expressDao.findAllByUserIdOrderByIdDesc(user.getId());
    }

    public List<ExpressOrder> findByPayStateAndCanceled(Boolean paied,Boolean canceled) {
        ExpressOrder order = new ExpressOrder();
        order.setHasPay(false);
        order.setCanceled(false);
        return query(order);
    }

    public ExpressOrder findByUnifiedId(Long unifiedId) {
        return expressDao.findByUnifiedId(unifiedId);
    }

    public JSONArray moreInfo(UnifiedOrder order) {
        ExpressOrder expressOrder = findByUnifiedId(order.getId());
        JSONArray arr = newJSONArray();
        if (expressOrder.getType() == 0) {
            arr.add(item("类型","寄件"));
            arr.add(item("寄件人", expressOrder.getFrom()));
            arr.add(item("寄件电话", expressOrder.getFromPhone()));
            arr.add(item("收件人", expressOrder.getTo()));
            arr.add(item("身份证号", expressOrder.getPersonalId()));
            arr.add(item("收件电话", expressOrder.getToPhone()));
            arr.add(item("收件地址", expressOrder.getToWhere()));
            arr.add(item("内容物", expressOrder.getWhatIs()));
            arr.add(item("快递", expressOrder.getExName()));
            arr.add(item("快递单号", expressOrder.getExNo()));
        } else {
            arr.add(item("类型","取件"));
            arr.add(item("收件人", expressOrder.getFrom()));
            arr.add(item("收件电话", expressOrder.getFromPhone()));
            arr.add(item("取件短信", expressOrder.getSms()));
        }

        return arr;
    }

    private JSONObject item(String name, String from) {
        JSONObject it = newJSONObject();
        it.put("name", name);
        it.put("num", from);
        return it;
    }

    public List<ExpressOrder> findAllUnConfirmed() {
        ExpressOrder order = new ExpressOrder();
        order.setCanceled(Boolean.FALSE);
        order.setPriceConfirmed(Boolean.FALSE);
        
        return query(order);
    }

    public List<ExpressOrder> findAllConfirmedButNotFilled() {
        return expressDao.findByExNoNullAndHasPayOrderByIdDesc(Boolean.TRUE);
    }

    public List<ExpressOrder> findAllFilled() {
        return expressDao.findByExNoNotNullOrderByIdDesc();
    }
}
