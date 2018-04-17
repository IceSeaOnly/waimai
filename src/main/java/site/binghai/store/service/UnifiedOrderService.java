package site.binghai.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.store.entity.UnifiedOrder;
import site.binghai.store.enums.OrderStatusEnum;
import site.binghai.store.enums.PayBizEnum;
import site.binghai.store.service.dao.UnifiedOrderDao;

import java.util.List;

/**
 * Created by IceSea on 2018/4/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class UnifiedOrderService extends BaseService<UnifiedOrder> {
    @Autowired
    private UnifiedOrderDao dao;

    @Override
    protected JpaRepository<UnifiedOrder, Long> getDao() {
        return dao;
    }

    public List<UnifiedOrder> findByAppCode(PayBizEnum pbe, Integer page, Integer pageSize) {
        if (page == null || page < 0) page = 0;
        if (pageSize == null || pageSize < 0) pageSize = 100;
        return dao.findAllByAppCodeOrderByCreatedDesc(pbe.getCode(), new PageRequest(page, pageSize));
    }

    public List<UnifiedOrder> findByAppCodeAndRegionId(PayBizEnum pbe, Long categoryId, Integer page, Integer pageSize) {
        if (page == null || page < 0) page = 0;
        if (pageSize == null || pageSize < 0) pageSize = 100;
        return dao.findAllByAppCodeAndRegionIdOrderByCreatedDesc(pbe.getCode(), categoryId, new PageRequest(page, pageSize));
    }

    public Long countByCode(PayBizEnum pb) {
        return dao.countByAppCode(pb.getCode());
    }

    public Long countByCodeAndRegionId(PayBizEnum pb, Long categoryId) {
        return dao.countByAppCodeAndRegionId(pb.getCode(), categoryId);
    }

    public Long countByAppCodeAndRegionIdAndStatus(PayBizEnum pb, Long categoryId,Integer status) {
        return dao.countByAppCodeAndRegionIdAndStatus(pb.getCode(), categoryId,status);
    }

    public List<UnifiedOrder> list(PayBizEnum payBiz, OrderStatusEnum status, Integer page, Integer pageSize, Long categoryId) {
        if (page == null || page < 0) page = 0;
        if (pageSize == null || pageSize < 0) pageSize = 100;

        return dao.findAllByAppCodeAndRegionIdAndStatusOrderByCreatedDesc(payBiz.getCode(), categoryId, status.getCode(), new PageRequest(page, pageSize));
    }
}
