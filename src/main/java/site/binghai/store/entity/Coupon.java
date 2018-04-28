package site.binghai.store.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Created by IceSea on 2018/4/4.
 * GitHub: https://github.com/IceSeaOnly
 */
@Data
@Entity
public class Coupon extends BaseEntity{
    @Id
    @GeneratedValue
    private Long id;
    private Integer appCode; // 可用支付场景
    private Long couponId;
    private Long orderId; //关联的订单id
    private Long userId;
    private Long regionId;
    private String openId;
    private String userName;
    private String useTime; // 使用时间
    private Long outOfDateTs;

    @Transient
    private Object extra;
    /**
     * {@link site.binghai.store.enums.CouponStatusEnum}
     * */
    private Integer couponStatus;

    @Override
    public Long getId() {
        return id;
    }
}
