package site.binghai.store.service;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import site.binghai.store.entity.Category;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IceSea on 2018/4/7.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class CategoryService extends BaseService<Category> {
    /**
     * 获取所有类目
     * superCate 为true时仅提取父类目
     * superCate 为false时仅提取子类目
     */
    public List<Category> list(boolean superCate) {
        List<Category> list = findAll(9999);
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        return list.stream()
                .filter(v -> superCate ? v.getFid() == null : v.getFid() != null)
                .collect(Collectors.toList());
    }
}
