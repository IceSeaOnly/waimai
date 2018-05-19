package site.binghai.store.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import site.binghai.store.controller.BaseController;
import site.binghai.store.entity.RefereeRecord;
import site.binghai.store.entity.User;
import site.binghai.store.service.RefereeRecordService;
import site.binghai.store.service.UserService;
import site.binghai.store.tools.TimeTools;

/**
 * Created by IceSea on 2018/5/19.
 * GitHub: https://github.com/IceSeaOnly
 */
@Controller
@RequestMapping("/user/")
public class ActivityController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private RefereeRecordService refereeRecordService;

    /**
     * 重定向到拉新页
     */
    @RequestMapping("toPullNewerPage")
    public String toPullNewerPage() {
        return "redirect:http://wmpay.binghai.site/newerActivity.php?from=" + getUser().getId();
    }

    @RequestMapping("pullNewer")
    public String pullNewer(Long from, ModelMap map) {
        User user = userService.findById(getUser().getId());
        if (TimeTools.currentTS() - user.getCreated() < 60000 && user.getRefereeId() == null) {
            RefereeRecord refereeRecord = new RefereeRecord();
            refereeRecord.setFromId(from);
            refereeRecord.setUserId(user.getId());
            refereeRecord.setPaid(Boolean.FALSE);
            refereeRecordService.save(refereeRecord);
            user.setRefereeId(from);
            userService.update(user);
            getSession().setAttribute("user", user);

            return "redirect:http://wmpay.binghai.site/followUs.html";
        } else {
            return commonResp("您已经是老用户啦!",
                    "您已经是老用户了，机会留给新人吧!现在邀请新人，最高可得88元!",
                    "去邀请新人", "http://wmpay.binghai.site/newerActivity.php", map);
        }
    }
}
