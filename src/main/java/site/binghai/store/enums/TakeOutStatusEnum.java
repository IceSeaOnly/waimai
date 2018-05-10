package site.binghai.store.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/17.
 * GitHub: https://github.com/IceSeaOnly
 */
public enum TakeOutStatusEnum {
    WAITING_PAY(0,"待支付"),
    WAITING_ACCEPT(1,"待接单"),
    PRODUCTING(2,"制作中"),
    SENDING(3,"配送中"),
    ENDED(4,"已结束"),
    MANAGAER_DOING(5,"管理流程中"),
    USER_RECEIVED_CONFIRMED(6,"已确认收货"),

    ;

    private int code;
    private String name;
    private static Map<Integer, TakeOutStatusEnum> maps;

    static {
        maps = new HashMap<>();
        for (TakeOutStatusEnum f : TakeOutStatusEnum.values()) {
            maps.put(f.code, f);
        }
    }

    public static TakeOutStatusEnum valueOf(Integer code){
        return maps.get(code);
    }

    TakeOutStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
