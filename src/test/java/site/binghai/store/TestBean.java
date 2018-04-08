package site.binghai.store;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import site.binghai.store.entity.TradeItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/8.
 * GitHub: https://github.com/IceSeaOnly
 */
public class TestBean {
    @Test
    public void beanTest() throws Exception {
        Map map = new HashMap();
        map.put("id",1);
        map.put("categoryId",11);
        map.put("price",15);
        map.put("saleCount",16);
        map.put("name","ThisIsName");
        map.put("detail","ThisIsDetail");
        map.put("img","ThisIsImg");

        JSONObject obj = JSONObject.parseObject(JSONObject.toJSONString(map));
        TradeItem tradeItem = obj.toJavaObject(TradeItem.class);

        map.put("saleCount",17);

        JSONObject item = JSONObject.parseObject(JSONObject.toJSONString(tradeItem));
        item.putAll(map);
        TradeItem newOne = item.toJavaObject(TradeItem.class);
    }
}
