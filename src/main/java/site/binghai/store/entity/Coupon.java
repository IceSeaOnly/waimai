package site.binghai.store.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
    private Long couponId;
    private Long userId;
    private String openId;
    private String userName;
    private Long outOfDateTs;
    /**
     * {@link site.binghai.store.enums.CouponStatusEnum}
     * */
    private Integer couponStatus;

    @Override
    public Long getId() {
        return id;
    }
}
