package site.binghai.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.store.entity.Coupon;
import site.binghai.store.entity.CouponTicket;
import site.binghai.store.entity.UnifiedOrder;
import site.binghai.store.entity.User;
import site.binghai.store.enums.CouponStatusEnum;
import site.binghai.store.service.dao.UserCouponDao;
import site.binghai.store.tools.TimeTools;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by IceSea on 2018/4/24.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class UserCouponService extends BaseService<Coupon> {
    @Autowired
    private UserCouponDao userCouponDao;
    @Autowired
    private CouponTicketService couponTicketService;

    public List<Coupon> listByUser(User user) {
        return userCouponDao.findByUserId(user.getId());
    }

    /**
     * 补充详细信息到extra字段
     */
    public void couponInfo(List<Coupon> couponList) {
        for (Coupon coupon : couponList) {
            coupon.setExtra(couponTicketService.findById(coupon.getCouponId()));
        }
    }

    /**
     * 退还使用的优惠券
     */
    @Transactional
    public void rollBackCoupon(UnifiedOrder order) {
        if (order.getCouponId() == null) return;
        Coupon coupon = findById(order.getCouponId());
        if (coupon == null) return;
        coupon.setCouponStatus(CouponStatusEnum.AVAILABLE.getCode());
        coupon.setOrderId(null);

        update(coupon);
        order.setShouldPay(order.getOriginalPrice());
        order.setCouponId(null);
    }

    @Transactional
    public void bindOrder(Long cpId, UnifiedOrder order) {
        if (order.getCouponId() != null) {
            rollBackCoupon(order);
        }

        Coupon coupon = findById(cpId);
        if (coupon == null) return;
        if (!coupon.getCouponStatus().equals(CouponStatusEnum.AVAILABLE.getCode())) return;

        coupon.setOrderId(order.getId());
        coupon.setCouponStatus(CouponStatusEnum.USED.getCode());
        coupon.setUseTime(TimeTools.format(TimeTools.currentTS()));
        update(coupon);

        order.setCouponId(cpId);

        CouponTicket couponTicket = couponTicketService.findById(coupon.getCouponId());
        switch (couponTicket.couponType()) {
            case FREE_ORDER:
                order.setShouldPay(0);
                break;
            case MINUS_PRICE:
                order.setShouldPay(order.getOriginalPrice() - couponTicket.getVal());
                break;
            case FULL_DISCOUNT:
                int max = Math.min(order.getOriginalPrice() * couponTicket.getVal() / 100, couponTicket.getDiscountLimit());
                order.setShouldPay(order.getOriginalPrice() - max);
        }
    }

    public Coupon findByTicketId(Long id) {
        return userCouponDao.findByCouponId(id);
    }

    @Transactional
    public void outOfDate() {
        List<Coupon> ls = userCouponDao.findByOutOfDateTsBeforeAndCouponStatus(TimeTools.currentTS(), CouponStatusEnum.AVAILABLE.getCode());
        ls.forEach(v -> {
            v.setCouponStatus(CouponStatusEnum.OUT_OF_DATE.getCode());
            update(v);
        });

        logger.info("{} coupon(s) out of date.", ls.size());
    }
}