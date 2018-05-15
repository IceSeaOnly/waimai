package site.binghai.store.entity;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IceSea on 2018/4/28.
 * GitHub: https://github.com/IceSeaOnly
 */
@Entity
@Data
public class RegionConfig extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long regionId;
    @Column(columnDefinition = "TEXT")
    private String indexScript;
    private String deliveryExplain; // 寄快递说明
    private String fetchExplain; // 取快递说明
    private Integer fetchFee; // 取快递价格
    private Integer fruitMinCost; // 水果最低起送价

    private String printerUser;
    private String printerKey;
    private String printerSN;
}
