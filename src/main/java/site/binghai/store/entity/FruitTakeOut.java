package site.binghai.store.entity;

import lombok.Data;
import site.binghai.store.enums.TakeOutStatusEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IceSea on 2018/4/5.
 * GitHub: https://github.com/IceSeaOnly
 * 水果外卖订单
 */
@Entity
@Data
public class FruitTakeOut extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long unifiedOrderId;
    private Long addressId;
    private String tradeItemJson;
    private String remark;
    private String orderId;
    /**
     * {@link TakeOutStatusEnum}
     * */
    private Integer takeOutStatus;

    @Override
    public Long getId() {
        return id;
    }
}
