package site.binghai.store.entity;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IceSea on 2018/4/4.
 * GitHub: https://github.com/IceSeaOnly
 * 优惠券
 */
@Entity
@Data
public class CouponTicket extends BaseEntity{
    @Id
    @GeneratedValue
    private Long id;
    /**
     * 优惠类型 {@link site.binghai.store.enums.CouponTypeEnum}
     * */
    private Integer couponType;
    private Integer val; // 该券的值，根据不同优惠类型代表不同含义
    private Long SpecificProduct; // 特定产品使用,暂时不用
    private Integer remaining; // 剩余量
    private Long activityStartTime;
    private Long activityEndTime;
    private Long regionId;

    @Override
    public Long getId() {
        return id;
    }
}
