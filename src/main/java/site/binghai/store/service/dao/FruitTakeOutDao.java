package site.binghai.store.service.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.store.entity.FruitTakeOut;

import java.util.List;

/**
 * Created by IceSea on 2018/4/8.
 * GitHub: https://github.com/IceSeaOnly
 */
public interface FruitTakeOutDao extends JpaRepository<FruitTakeOut, Long> {
    List<FruitTakeOut> findAllByOrderByCreatedDesc(Pageable pageable);
}
