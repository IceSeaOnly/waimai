package site.binghai.store.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.store.entity.Category;
import site.binghai.store.service.CategoryService;

import java.util.List;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/7.
 * GitHub: https://github.com/IceSeaOnly
 */
@RestController
@RequestMapping("/admin/cate/")
public class CategoryController extends BaseController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列出类目信息，传入superCate时仅返回父类目，不传则只返回子类目
     */
    @RequestMapping("list")
    public Object list(Integer superCate) {
        if (superCate != null) {
            return success(categoryService.list(true), "SUCCESS");
        } else {
            return success(categoryService.list(false), "SUCCESS");
        }
    }

    /**
     * 列出所有类目及其商品量
     */
    @RequestMapping("listCate")
    public Object listCate() {
        List<Category> list = categoryService.list(false);
        if (CollectionUtils.isEmpty(list)) {
            return success();
        }

        JSONArray lists = new JSONArray();
        for (Category cate : list) {
            JSONObject obj = newJSONObject();
            obj.put("name", cate.getFName() + "/" + cate.getName());
            obj.put("id", cate.getId());
            obj.put("goodsNum", 0);

            lists.add(obj);
        }

        JSONObject data = newJSONObject();
        data.put("lists", lists);

        return success(data, "SUCCESS");
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public Object update(@RequestBody Map map) {
        String name = map.get("name").toString();
        Long id = Long.valueOf(map.get("id").toString());
        Category category = categoryService.findById(id);
        if (category != null) {
            logger.warn("{} update category {} to name {}", getAdmin(), category, name);
            category.setName(name);
            categoryService.update(category);
        }
        return success();
    }

    @RequestMapping("delete")
    public Object delete(@RequestParam Long id) {
        categoryService.deleteIfExist(id);
        return success();
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Object add(@RequestBody Map map) {
        Object cateName = map.get("name");
        Object fid = map.get("fid");
        if (cateName == null) {
            return fail("类目名必传!");
        }

        if (fid != null && !NumberUtils.isNumber(fid.toString())) {
            logger.error("fid error:{}", fid);
            return fail("父类目非法!");
        }

        Category fCat = null;
        if (fid != null) {
            Long id = Long.valueOf(fid.toString());
            if (id > 0 && (fCat = categoryService.findById(id)) == null) {
                return fail("父类目不存在!");
            }
        }

        Category one = new Category();
        one.setName(cateName.toString());
        if (fCat != null) {
            one.setFid(fCat.getId());
            one.setFName(fCat.getName());
        }

        one = categoryService.save(one);

        return success(one, "SUCCESS");
    }

    @RequestMapping("categoryTree")
    public Object categoryTree() {
        List<Category> supers = categoryService.list(true);
        if (CollectionUtils.isEmpty(supers)) {
            return success();
        }

        List<JSONObject> ls = emptyList();

        for (Category cate : supers) {
            JSONObject item = newJSONObject();
            item.put("label", cate.getName());
            item.put("value", cate.getId());
            JSONArray arr = newJSONArray();
            List<Category> chils = categoryService.listAllByFid(cate.getId());
            for (Category ct : chils) {
                JSONObject obj = newJSONObject();
                obj.put("label", ct.getName());
                obj.put("value", ct.getId().toString());
                arr.add(obj);
            }
            item.put("children", arr);
            ls.add(item);
        }

        return success(ls, "SUCCESS");
    }
}
