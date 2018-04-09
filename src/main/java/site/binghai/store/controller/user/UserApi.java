package site.binghai.store.controller.user;

import com.qiniu.util.Auth;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.store.controller.BaseController;

/**
 * Created by IceSea on 2018/4/8.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("/")
@RestController
public class UserApi extends BaseController {

    @RequestMapping("qiniuToken")
    public Object qiNiu() {
        String accessKey = "34umHsbJ1nxg23yfWfBI3IQ9duQ6ES08KP625MGw";
        String secretKey = "SrGc1rRcRniNDthc-qSjbb16TSp8-HdERs5DApR4";
        String bucket = "icesea-public";
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        return success(upToken, "SUCCESS");
    }
}
