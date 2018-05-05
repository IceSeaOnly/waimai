package site.binghai.store.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IceSea on 2018/5/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@Entity
@Data
public class City extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String province;
    private String city;
    private String fkg; // 首重费
    private String xkg; // 续重费
}
