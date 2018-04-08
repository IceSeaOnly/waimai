package site.binghai.store.service;

import org.springframework.stereotype.Service;
import site.binghai.store.entity.TradeItem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IceSea on 2018/4/8.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class TradeItemService extends BaseService<TradeItem> {

    public List<TradeItem> findByCategoryId(Long id) {
        List<TradeItem> all = findAll(9999);
        return all.stream().filter(v -> v.getCategoryId().equals(id))
                .collect(Collectors.toList());
    }
}
