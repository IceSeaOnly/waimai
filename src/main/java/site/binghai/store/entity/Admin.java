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
public class Admin extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String userName;
    private String passWord;
    private Boolean superAdmin;
    private Boolean banned;

    public Admin() {
        superAdmin = false;
        banned = false;
    }
}
