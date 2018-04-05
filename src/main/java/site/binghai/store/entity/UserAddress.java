package site.binghai.store.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IceSea on 2018/4/5.
 * GitHub: https://github.com/IceSeaOnly
 * 记录用户使用过的地址
 */
@Entity
@Data
public class UserAddress extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private String addressHead;
    private String addressDetail;
    private String userName;
    private String userPhone;

    @Override
    public Long getId() {
        return id;
    }


}
