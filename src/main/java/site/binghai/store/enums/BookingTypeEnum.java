package site.binghai.store.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IceSea on 2018/5/5.
 * GitHub: https://github.com/IceSeaOnly
 */
public enum BookingTypeEnum {
    DELIVER_EXPRESS(0, "寄快递"),
    FETCH_EXPRESS(1, "取快递"),

    ;

    private int code;
    private String name;

    BookingTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    private static Map<Integer, BookingTypeEnum> maps;

    static {
        maps = new HashMap<>();
        for (BookingTypeEnum status : BookingTypeEnum.values()) {
            maps.put(status.code, status);
        }
    }

    public static BookingTypeEnum valueOf(Integer code){
        return maps.get(code);
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
