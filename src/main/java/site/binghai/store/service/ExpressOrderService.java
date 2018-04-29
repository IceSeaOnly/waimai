package site.binghai.store.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
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
public class ExpressOrderService extends BaseService<ExpressOrder> {
    @Autowired
    private ExpressDao expressDao;

    public List<ExpressOrder> findAllPaiedDescById(Integer page, Integer pageSize) {
        return expressDao.findAllByHasPayOrderByIdDesc(true, new PageRequest(page, pageSize));
    }

    public List<ExpressOrder> findAllByUserId(User user) {
        return expressDao.findAllByUserIdOrderByIdDesc(user.getId());
    }

    public List<ExpressOrder> findByPayState(Boolean paied) {
        return expressDao.findByHasPay(paied);
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
            arr.add(item("收件电话", expressOrder.getToPhone()));
            arr.add(item("收件地址", expressOrder.getToWhere()));
            arr.add(item("内容物", expressOrder.getWhatIs()));
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
}
