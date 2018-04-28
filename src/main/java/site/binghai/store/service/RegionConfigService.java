package site.binghai.store.service;

import org.springframework.stereotype.Service;
import site.binghai.store.entity.RegionConfig;

/**
 * Created by IceSea on 2018/4/28.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class RegionConfigService extends BaseService<RegionConfig> {

    public RegionConfig findByRegionId(Long regionId) {
        RegionConfig config = new RegionConfig();
        config.setRegionId(regionId);
        return queryOne(config);
    }
}
