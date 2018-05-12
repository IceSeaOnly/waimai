package site.binghai.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.store.entity.Coupon;
import site.binghai.store.entity.CouponTicket;
import site.binghai.store.entity.UnifiedOrder;
import site.binghai.store.entity.User;
import site.binghai.store.enums.CouponStatusEnum;
import site.binghai.store.enums.OrderStatusEnum;
import site.binghai.store.service.dao.UserCouponDao;
import site.binghai.store.tools.TimeTools;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

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
        order.setOrderId(UUID.randomUUID().toString().substring(9));
    }

    @Transactional
    public void bindOrder(Long cpId, UnifiedOrder order) throws Exception {
        if (cpId == null || order == null) {
            throw new Exception("参数不得为空");
        }
        if (order.getCouponId() != null) {
            rollBackCoupon(order);
        }

        order.setOrderId(UUID.randomUUID().toString().substring(9));

        if (order.getStatus() >= OrderStatusEnum.PAIED.getCode()) {
            throw new Exception("订单已支付,优惠券不可用");
        }

        Coupon coupon = findById(cpId);

        if (coupon == null) {
            throw new Exception("优惠券不存在!");
        }

        if (!coupon.getCouponStatus().equals(CouponStatusEnum.AVAILABLE.getCode())) {
            throw new Exception("优惠券不可用!");
        }

        if (!coupon.getAppCode().equals(order.getAppCode())) {
            throw new Exception("使用场景不同!");
        }

        if (!coupon.getRegionId().equals(order.getRegionId())) {
            throw new Exception("使用区域不同!");
        }

        if (!coupon.getUserId().equals(order.getUserId())) {
            throw new Exception("优惠券所属错误!");
        }

        coupon.setOrderId(order.getId());
        coupon.setCouponStatus(CouponStatusEnum.USED.getCode());
        coupon.setUseTime(TimeTools.format(TimeTools.currentTS()));
        update(coupon);

        order.setCouponId(cpId);

        CouponTicket couponTicket = couponTicketService.findById(coupon.getCouponId());
        switch (couponTicket.couponType()) {
            case FREE_ORDER:
                order.setShouldPay(1);
                break;
            case MINUS_PRICE:
                order.setShouldPay(order.getOriginalPrice() - couponTicket.getVal());
                break;
            case FULL_DISCOUNT:
                int test = order.getOriginalPrice() * couponTicket.getVal() / 1000;
                int max = Math.max(test, order.getShouldPay() - couponTicket.getDiscountLimit());
                order.setShouldPay(max);
        }
        if (order.getShouldPay() <= 0) {
            order.setShouldPay(1);
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

    public Coupon findByTicketIdAndUserId(Long couponId, Long userId) {
        return userCouponDao.findByCouponIdAndUserId(couponId, userId);
    }
}
