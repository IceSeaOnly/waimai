package site.binghai.store.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/4.
 * GitHub: https://github.com/IceSeaOnly
 */
@Getter
public enum CouponStatusEnum {
    AVAILABLE(0,"可用"),
    USED(1,"已用"),
    OUT_OF_DATE(2,"过期"),
    INVALID(3,"作废"),
    ;


    private int code;
    private String name;
    private static Map<Integer, CouponStatusEnum> maps;

    static {
        maps = new HashMap<>();
        for (CouponStatusEnum status : CouponStatusEnum.values()) {
            maps.put(status.code, status);
        }
    }

    public static CouponStatusEnum valueOf(int code) {
        return maps.get(code);
    }

    CouponStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
