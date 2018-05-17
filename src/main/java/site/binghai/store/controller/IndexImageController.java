package site.binghai.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.store.entity.IndexImage;
import site.binghai.store.service.IndexImageService;

import java.util.Map;

/**
 * Created by IceSea on 2018/5/17.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("/admin/indexImage/")
@RestController
public class IndexImageController extends BaseController {
    @Autowired
    private IndexImageService indexImageService;

    @RequestMapping("list")
    public Object list() {
        return success(indexImageService.findAll(9999), null);
    }

    @RequestMapping("delete")
    public Object delete(@RequestParam Long id) {
        IndexImage indexImage = indexImageService.findById(id);
        logger.warn("admin {} deleted index image {}", getAdmin(), indexImage);
        indexImageService.delete(id);
        return success();
    }

    @PostMapping("add")
    public Object add(@RequestBody Map map) {
        IndexImage image = indexImageService.newInstance(map);
        return success(indexImageService.save(image), null);
    }
}
