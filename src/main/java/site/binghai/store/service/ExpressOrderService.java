package site.binghai.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import site.binghai.store.entity.ExpressOrder;
import site.binghai.store.entity.User;
import site.binghai.store.service.dao.ExpressDao;

import java.util.List;

/**
 * Created by IceSea on 2018/4/27.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class ExpressOrderService extends BaseService<ExpressOrder> {
    @Autowired
    private ExpressDao expressDao;

    public List<ExpressOrder> findAllPaiedDescById(Integer page, Integer pageSize) {
        return expressDao.findAllByHasPayOrderByIdDesc(true,new PageRequest(page, pageSize));
    }

    public List<ExpressOrder> findAllByUserId(User user){
        return expressDao.findAllByUserIdOrderByIdDesc(user.getId());
    }

    public List<ExpressOrder> findByPayState(Boolean paied){
        return expressDao.findByHasPay(paied);
    }

    public ExpressOrder findByUnifiedId(Long unifiedId) {
        return expressDao.findByUnifiedId(unifiedId);
    }
}
