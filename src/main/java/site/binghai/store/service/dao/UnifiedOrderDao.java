package site.binghai.store.service.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.store.entity.UnifiedOrder;

import java.util.List;

/**
 * Created by IceSea on 2018/4/12.
 * GitHub: https://github.com/IceSeaOnly
 */
public interface UnifiedOrderDao extends JpaRepository<UnifiedOrder, Long> {
    List<UnifiedOrder> findAllByAppCodeOrderByCreatedDesc(Integer code, Pageable pageable);

    List<UnifiedOrder> findAllByAppCodeAndRegionIdOrderByCreatedDesc(Integer code, Long regionId, Pageable pageable);

    List<UnifiedOrder> findAllByAppCodeAndRegionIdAndStatusOrderByCreatedDesc(Integer code, Long regionId, Integer status, Pageable pageable);

    Long countByAppCode(Integer code);

    Long countByAppCodeAndRegionId(Integer code, Long regionId);
    Long countByAppCodeAndRegionIdAndStatus(Integer code, Long regionId,Integer status);
}
