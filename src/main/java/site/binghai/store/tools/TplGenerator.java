package site.binghai.store.tools;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by IceSea on 2018/4/30.
 * GitHub: https://github.com/IceSeaOnly
 * 模板消息构造器
 */
public class TplGenerator {
    private JSONObject data;

    private TplGenerator() {
        this.data = data = new JSONObject();
    }

    public static TplGenerator getInstance() {
        return new TplGenerator();
    }

    public TplGenerator put(String key, String value) {
        JSONObject v = new JSONObject();
        v.put("value", value);
        data.put(key, v);
        return this;
    }

    public JSONObject getAll() {
        return data;
    }
}
