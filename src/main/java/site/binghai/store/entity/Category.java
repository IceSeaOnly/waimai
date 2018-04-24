package site.binghai.store.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IceSea on 2018/4/7.
 * GitHub: https://github.com/IceSeaOnly
 * 商品类目
 */
@Entity
@Data
public class Category extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Long fid; // 父级id
    private String fName; // 父级类目名
    /**
     * 如果是超级类目
     * 超级类目代表一个地区，这里是该地区首页附加代码
     * */
    @Column(columnDefinition = "TEXT")
    private String indexScript;
}
