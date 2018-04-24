package site.binghai.store.tools;

import org.apache.commons.lang3.StringUtils;
import site.binghai.store.entity.User;

/**
 * Created by IceSea on 2018/4/23.
 * GitHub: https://github.com/IceSeaOnly
 */
public class UserTool {

    public static boolean isInfoMiss(User instance) {
        return StringUtils.isEmpty(instance.getPhone())
                || instance.getRegionId() == null
                || StringUtils.isEmpty(instance.getUserName());
    }
}
