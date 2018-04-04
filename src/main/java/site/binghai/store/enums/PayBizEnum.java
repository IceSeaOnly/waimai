package site.binghai.store.enums;


import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/4.
 * GitHub: https://github.com/IceSeaOnly
 */
@Getter
public enum PayBizEnum {
    FRUIT_TAKE_OUT(0, "水果外卖"),

    ;

    private int code;
    private String name;
    private static Map<Integer, PayBizEnum> maps;

    static {
        maps = new HashMap<>();
        for (PayBizEnum f : PayBizEnum.values()) {
            maps.put(f.code, f);
        }
    }

    public static PayBizEnum valueOf(int code) {
        return maps.get(code);
    }

    PayBizEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
