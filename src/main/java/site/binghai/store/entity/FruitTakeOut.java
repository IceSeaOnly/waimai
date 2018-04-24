package site.binghai.store.entity;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import site.binghai.store.enums.TakeOutStatusEnum;

import javax.persistence.Column;
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
    private Long userId;
    private Long unifiedOrderId;
    private Long addressId;
    @Column(columnDefinition = "TEXT")
    private String tradeItemJson;
    private String remark;

    /**
     * {@link TakeOutStatusEnum}
     */
    private Integer takeOutStatus;

    public JSONArray getItemJSONObject() {
        return JSONArray.parseArray(tradeItemJson);
    }

    public int getItemNum() {
        int sum = 0;
        JSONArray arr = getItemJSONObject();
        for (int i = 0; i < arr.size(); i++) {
            sum += arr.getJSONObject(i).getInteger("num");
        }
        return sum;
    }

    public TakeOutStatusEnum getState() {
        return TakeOutStatusEnum.valueOf(takeOutStatus);
    }

    @Override
    public Long getId() {
        return id;
    }
}
