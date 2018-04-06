package site.binghai.store.service;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import site.binghai.store.entity.Admin;

import java.util.List;

/**
 * Created by IceSea on 2018/4/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class AdminService extends BaseService<Admin> {

    public Admin adminLogin(String userName, String pass) {
        List<Admin> adminList = findAll(1000);
        if (CollectionUtils.isEmpty(adminList)) {
            return null;
        }
        for (Admin admin : adminList) {
            if (admin.getBanned() || admin.isHasDeleted()) {
                continue;
            }

            if (admin.getUserName().equals(userName) && admin.getPassWord().equals(pass)) {
                return admin;
            }
        }
        return null;
    }
}
