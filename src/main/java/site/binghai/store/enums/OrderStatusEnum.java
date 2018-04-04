package site.binghai.store.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/4.
 * GitHub: https://github.com/IceSeaOnly
 */
@Getter
public enum OrderStatusEnum {
    CREATED(0, "已创建"),
    PAYING(1, "待支付"),
    PAIED(2, "已支付"),
    PROCESSING(3, "订单处理中"),
    COMPLETE(4, "已完成"),
    REFUNDING(5, "退款申请中"),
    CANCELED_REFUNDED(6, "已退款/已取消"),
    CANCELED(7, "已取消"),
    OUTOFDATE(8, "已超时/已取消"),

    ;


    private int code;
    private String name;
    private static Map<Integer, OrderStatusEnum> maps;

    static {
        maps = new HashMap<>();
        for (OrderStatusEnum status : OrderStatusEnum.values()) {
            maps.put(status.code, status);
        }
    }

    public static OrderStatusEnum valueOf(int code) {
        return maps.get(code);
    }

    OrderStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
