package site.binghai.store.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IceSea on 2018/5/19.
 * GitHub: https://github.com/IceSeaOnly
 */
@Entity
@Data
public class RefereeRecord extends BaseEntity{
    @Id
    @GeneratedValue
    private Long id;
    private Long fromId; // 推荐人id
    private Long userId; // 被推荐人id
    private Boolean paid;
}
