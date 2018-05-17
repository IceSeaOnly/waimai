package site.binghai.store.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IceSea on 2018/4/23.
 * GitHub: https://github.com/IceSeaOnly
 */
@Entity
@Data
public class IndexImage extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String img;
    private String href;
}
