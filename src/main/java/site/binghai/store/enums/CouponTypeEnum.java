package site.binghai.store.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/4.
 * GitHub: https://github.com/IceSeaOnly
 */
@Getter
public enum CouponTypeEnum {
    FULL_DISCOUNT(0,"折扣券"),
    MINUS_PRICE(1,"立减券"),
    FREE_ORDER(2,"免单券"),

    ;


    private int code;
    private String name;
    private static Map<Integer, CouponTypeEnum> maps;

    static {
        maps = new HashMap<>();
        for (CouponTypeEnum status : CouponTypeEnum.values()) {
            maps.put(status.code, status);
        }
    }

    public static CouponTypeEnum valueOf(int code) {
        return maps.get(code);
    }

    CouponTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
