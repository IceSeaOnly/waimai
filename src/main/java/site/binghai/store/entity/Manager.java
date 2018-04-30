package site.binghai.store.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IceSea on 2018/4/30.
 * GitHub: https://github.com/IceSeaOnly
 */
@Data
@Entity
public class Manager extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String openId;
    private Long regionId;
    private Long userId;
}
