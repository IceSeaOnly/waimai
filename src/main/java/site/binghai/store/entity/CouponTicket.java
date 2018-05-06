package site.binghai.store.entity;


import lombok.Data;
import site.binghai.store.enums.CouponTypeEnum;
import site.binghai.store.enums.PayBizEnum;
import site.binghai.store.tools.TimeTools;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

/**
 * Created by IceSea on 2018/4/4.
 * GitHub: https://github.com/IceSeaOnly
 * 优惠券
 */
@Entity
@Data
public class CouponTicket extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String uuid;
    /**
     * 优惠类型 {@link site.binghai.store.enums.CouponTypeEnum}
     */
    private Integer couponType;
    private Integer appCode;
    private Integer val; // 该券的值，根据不同优惠类型代表不同含义
    private Long SpecificProduct; // 特定产品使用,暂时不用
    private Integer remaining; // 剩余量
    private Long activityStartTime;
    private Long activityEndTime;
    private Long regionId;
    private String regionName;
    private Integer discountLimit; // 折扣券上限

    public CouponTicket() {
        uuid = UUID.randomUUID().toString();
    }

    @Override
    public Long getId() {
        return id;
    }

    public PayBizEnum payBizType() {
        return PayBizEnum.valueOf(appCode);
    }

    public CouponTypeEnum couponType() {
        return CouponTypeEnum.valueOf(couponType);
    }

    public String getAvailableTime() {
        return TimeTools.format(activityEndTime);
    }

    /** 仅供前端调用 */
    @Deprecated
    public double doubleVal() {
        return val / 100.0;
    }

    public String getDescribe() {
        switch (couponType()) {
            case FULL_DISCOUNT:
                return "最高" + discountLimit / 100 + "元";
            case FREE_ORDER:
                return "立享免单";
            case MINUS_PRICE:
                return "立减" + val / 100 + "元";
        }
        return "";
    }
}
