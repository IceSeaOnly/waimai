package site.binghai.store.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.store.entity.User;
import site.binghai.store.service.dao.UserDao;

/**
 * Created by IceSea on 2018/4/8.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class UserService extends BaseService<User> {

    @Autowired
    private UserDao userDao;

    public User getByOpenId(String openId) {
        return userDao.findByOpenId(openId);
    }

    @Override
    protected JpaRepository<User, Long> getDao() {
        return userDao;
    }

}
