package site.binghai.store.service;

import org.springframework.stereotype.Service;
import site.binghai.store.entity.CouponTicket;

/**
 * Created by IceSea on 2018/4/24.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class CouponTicketService extends BaseService<CouponTicket> {

    public CouponTicket findByUUID(String uuid) {
        return findAll(9999).stream().filter(v -> v.getUuid().equals(uuid)).findFirst().orElse(null);
    }
}
