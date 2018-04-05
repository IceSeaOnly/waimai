package site.binghai.store.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IceSea on 2018/4/5.
 * GitHub: https://github.com/IceSeaOnly
 * 区域
 */
@Entity
@Data
public class Region extends BaseEntity{
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Boolean acitived;

    @Override
    public Long getId() {
        return id;
    }
}
