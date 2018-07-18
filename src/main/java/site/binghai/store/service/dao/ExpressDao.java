package site.binghai.store.service.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.store.entity.ExpressOrder;

import java.util.List;

/**
 * Created by IceSea on 2018/4/27.
 * GitHub: https://github.com/IceSeaOnly
 */
public interface ExpressDao extends JpaRepository<ExpressOrder,Long>{
    ExpressOrder findByUnifiedId(Long id);
    List<ExpressOrder> findAllByHasPayOrderByIdDesc(Boolean hasPay,Pageable pageable);
    List<ExpressOrder> findAllByUserIdOrderByIdDesc(Long userId);
    List<ExpressOrder> findByHasPay(Boolean pay);
    List<ExpressOrder> findByExNoNotNullOrderByIdDesc();
    List<ExpressOrder> findByExNoNullAndCanceledOrderByIdDesc(Boolean canceled);
}
