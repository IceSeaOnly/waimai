package site.binghai.store.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IceSea on 2018/4/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@Entity
@Data
public class User extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String userName;
    private String phone;
    private String openId;
    private Integer integral; // 会员积分
    private Long regionId; // 对应Category一级目录
    private String regionName;

}
