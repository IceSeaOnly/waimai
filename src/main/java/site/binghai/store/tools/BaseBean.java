package site.binghai.store.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/8.
 * GitHub: https://github.com/IceSeaOnly
 */
public abstract class BaseBean {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected JSONObject newJSONObject() {
        return new JSONObject();
    }

    protected JSONArray newJSONArray() {
        return new JSONArray();
    }

    public Long getLong(Map map, String key) {
        if (map == null || map.get(key) == null) return null;
        if (NumberUtils.isNumber(map.get(key).toString())) {
            return Long.valueOf(map.get(key).toString());
        }
        return null;
    }

    protected List emptyList() {
        return new ArrayList();
    }
}
