package site.binghai.store.controller.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import site.binghai.store.controller.BaseController;
import site.binghai.store.entity.*;
import site.binghai.store.service.*;

/**
 * Created by IceSea on 2018/4/23.
 * GitHub: https://github.com/IceSeaOnly
 */
@RequestMapping("/user/")
@Controller
public class IndexController extends BaseController {
    @Autowired
    private IndexImageService indexImageService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;
    @Autowired
    private RegionConfigService regionConfigService;


    @RequestMapping("index")
    public String index(ModelMap map) {
        map.put("imgs", indexImageService.findAll(10));
        return "userIndex";
    }

    @RequestMapping("indexScript")
    @ResponseBody
    public Object indexScript() {
        RegionConfig config = regionConfigService.findByRegionId(getUser().getRegionId());
        return config == null ? "" : config.getIndexScript();
    }


    /**
     * 用户页
     */
    @RequestMapping("member")
    public String member() {
        return "userMember";
    }


    /**
     * 信息页
     */
    @RequestMapping("memberInfo")
    public String memberInfo(ModelMap map) {
        UserAddress address = addressService.getUserAddress(getUser().getId());
        map.put("address", address == null ? "" : address.getAddressDetail());
        map.put("regions", categoryService.list(true));
        return "userMemberInfo";
    }

    @RequestMapping(value = "updateMemberInfo", method = RequestMethod.POST)
    public String updateMemberInfo(@RequestParam String userName,
                                   @RequestParam String phone,
                                   @RequestParam String address,
                                   @RequestParam Long regionId,

                                   ModelMap map) {
        Category region = categoryService.findById(regionId);
        if (region == null || StringUtils.isEmpty(userName) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(address)) {
            return commonResp("输入有误!", "您的输入不正确!", "重新输入", "/user/memberInfo", map);
        }

        User user = getUser();
        user.setRegionId(regionId);
        user.setRegionName(region.getName());
        user.setPhone(phone);
        user.setUserName(userName);

        userService.update(user);

        UserAddress userAddress = new UserAddress();
        userAddress.setAddressDetail(address);
        userAddress.setAddressHead(region.getName());
        userAddress.setUserName(userName);
        userAddress.setUserPhone(phone);
        userAddress.setUserId(user.getId());

        addressService.save(userAddress);

        return commonResp("更新成功", "", "好的", "/user/member", map);
    }
}
