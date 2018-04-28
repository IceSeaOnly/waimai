package site.binghai.store.entity;



import site.binghai.store.tools.TimeTools;

import javax.persistence.MappedSuperclass;

/**
 * Created by IceSea on 2018/4/2.
 * GitHub: https://github.com/IceSeaOnly
 */
@MappedSuperclass
public abstract class BaseEntity {
    private Long created;
    private String createdTime;

    public BaseEntity() {
        created = TimeTools.currentTS();
        createdTime = TimeTools.format(created);
    }


    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public abstract Long getId();
}
