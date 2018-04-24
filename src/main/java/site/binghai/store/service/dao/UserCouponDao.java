package site.binghai.store.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.store.entity.Coupon;

import java.util.List;

/**
 * Created by IceSea on 2018/4/24.
 * GitHub: https://github.com/IceSeaOnly
 */
public interface UserCouponDao extends JpaRepository<Coupon, Long> {
    List<Coupon> findByUserId(Long userid);
    Coupon findByCouponId(Long id);
    List<Coupon> findByOutOfDateTsBeforeAndCouponStatus(Long ts,int state);
}
