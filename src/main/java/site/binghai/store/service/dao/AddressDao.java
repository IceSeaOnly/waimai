package site.binghai.store.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.store.entity.UserAddress;

import java.util.List;

/**
 * Created by IceSea on 2018/4/23.
 * GitHub: https://github.com/IceSeaOnly
 */
public interface AddressDao extends JpaRepository<UserAddress, Long> {
    List<UserAddress> getByUserId(Long userId);
}
