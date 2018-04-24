package site.binghai.store.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IceSea on 2018/4/6.
 * GitHub: https://github.com/IceSeaOnly
 */
@Entity
@Data
public class TradeItem extends BaseEntity{
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Long categoryId;
    private Integer price;
    private String detail;
    private String img;
    private Integer saleCount;
    private Boolean onLine;
    private Integer remains; // 剩余

    public double getDoublePrice(){
        return price/100.0;
    }
}
