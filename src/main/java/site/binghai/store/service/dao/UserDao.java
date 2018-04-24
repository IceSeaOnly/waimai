package site.binghai.store.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.store.entity.User;

/**
 * Created by IceSea on 2018/4/23.
 * GitHub: https://github.com/IceSeaOnly
 */
public interface UserDao extends JpaRepository<User, Long> {
    User findByOpenId(String openid);
}
