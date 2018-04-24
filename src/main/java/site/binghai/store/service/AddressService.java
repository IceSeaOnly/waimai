package site.binghai.store.service;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.store.entity.UserAddress;
import site.binghai.store.service.dao.AddressDao;

import java.util.List;

/**
 * Created by IceSea on 2018/4/23.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class AddressService extends BaseService<UserAddress> {

    @Autowired
    private AddressDao addressDao;

    public UserAddress getUserAddress(Long userId) {
        List<UserAddress> list = addressDao.getByUserId(userId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.get(list.size() - 1);
    }


    @Override
    protected JpaRepository<UserAddress, Long> getDao() {
        return addressDao;
    }
}
