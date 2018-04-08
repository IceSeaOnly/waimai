package site.binghai.store.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.store.entity.Category;
import site.binghai.store.entity.TradeItem;
import site.binghai.store.service.CategoryService;
import site.binghai.store.service.TradeItemService;

import java.util.List;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/8.
 * GitHub: https://github.com/IceSeaOnly
 */
@RestController
@RequestMapping("/admin/tradeItem/")
public class TradeItemController extends BaseController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TradeItemService tradeItemService;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public Object list(@RequestParam Long categoryId) {
        Category category = null;
        category = categoryService.findById(categoryId);
        if (category == null) {
            return fail("categoryId error!");
        }

        JSONArray data = newJSONArray();

        if (category.getFid() == null) {
            data.add(listCategoryList(category));
        } else {
            List<Category> categories = categoryService.listAllByFid(categoryId);
            for (Category cat : categories) {
                data.add(listCategoryList(cat));
            }
        }

        return success(data, "SUCCESS");
    }

    /**
     * 列出某子类目下的所有商品
     */
    private List<TradeItem> listCategoryList(Category category) {
        if (category.getFid() != null) {
            return emptyList();
        }

        return tradeItemService.findByCategoryId(category.getId());
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Object add(@RequestBody Map map) {
        JSONObject obj = JSONObject.parseObject(JSONObject.toJSONString(map));
        TradeItem item = obj.toJavaObject(TradeItem.class);
        item.setId(null);

        return success(tradeItemService.save(item), "SUCCESS");
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public Object update(@RequestBody Map map) {
        TradeItem item = null;
        try {
            item = tradeItemService.updateAndSave(getAdmin(), map);
        } catch (Exception e) {
            logger.error("{} admin update tradeItem {} error!", getAdmin(), map, e);
            return fail(e.getMessage());
        }

        return success(item, "SUCCESS");
    }

    @RequestMapping("delete")
    public Object delete(@RequestParam Long id) {
        TradeItem tradeItem = tradeItemService.findById(id);
        if (tradeItem == null) {
            return fail("item not exist!");
        }

        logger.warn("{} delete tradeItem {}", getAdmin(), tradeItem);
        tradeItemService.delete(id);
        return success();
    }
}
