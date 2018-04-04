package site.binghai.store.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IceSea on 2018/4/4.
 * GitHub: https://github.com/IceSeaOnly
 * 统一订单
 */
@Entity
public class UnifiedOrder extends BaseEntity{
    @Id
    @GeneratedValue
    private Long id;

    @Override
    public Long getId() {
        return null;
    }
}
