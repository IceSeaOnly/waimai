package site.binghai.store.service;

import org.springframework.stereotype.Service;
import site.binghai.store.entity.Manager;

import java.util.List;

/**
 * Created by IceSea on 2018/4/30.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class ManagerService extends BaseService<Manager> {

    public List<Manager> findByRegionId(Long regionId) {
        Manager m = new Manager();
        m.setRegionId(regionId);
        return query(m);
    }
}
