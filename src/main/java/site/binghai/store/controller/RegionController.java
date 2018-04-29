package site.binghai.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.store.entity.RegionConfig;
import site.binghai.store.service.RegionConfigService;

import java.util.Map;

/**
 * Created by IceSea on 2018/4/28.
 * GitHub: https://github.com/IceSeaOnly
 */
@RestController
@RequestMapping("/admin/region/")
public class RegionController extends BaseController {
    @Autowired
    private RegionConfigService regionConfigService;

    @GetMapping("list")
    public Object list(Long regionId) {
        if (regionId == null) {
            return success(regionConfigService.findAll(999), "SUCCESS");
        } else {
            RegionConfig regionConfig = regionConfigService.findByRegionId(regionId);
            if (regionConfig == null) {
                regionConfig = new RegionConfig();
                regionConfig.setRegionId(regionId);
                regionConfig.setFetchFee(500);
                regionConfig = regionConfigService.save(regionConfig);
            }
            return success(regionConfig, "SUCCESS");
        }
    }

    @PostMapping("update")
    public Object update(@RequestBody Map map) {
        logger.warn("{} update regionConfig to {}", getAdmin(), map);
        try {
            regionConfigService.updateAndSave(getAdmin(), map);
        } catch (Exception e) {
            logger.error("{} update regionConfig failed.{}", getAdmin(), map, e);
            return fail("更新失败");
        }
        return success();
    }
}
