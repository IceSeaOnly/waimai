package site.binghai.store.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.store.config.IceConfig;
import site.binghai.store.tools.BaseBean;
import site.binghai.store.tools.HttpUtils;
import site.binghai.store.tools.TimeTools;

/**
 * Created by IceSea on 2018/4/23.
 * GitHub: https://github.com/IceSeaOnly
 */

@Service
public class WxService extends BaseBean {
    private static final String accUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    private static final String userInfo = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
    private static final String tplMessage = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
    @Autowired
    private IceConfig iceConfig;

    private String accessToken;
    private Long tokenOutOfTimeTs;

    public String getAccessToken() {
//        if (accessToken != null && TimeTools.currentTS() < tokenOutOfTimeTs) {
//            return accessToken;
//        }

        JSONObject res = HttpUtils.sendJSONGet(String.format(accUrl, iceConfig.getAppId(), iceConfig.getAppSecret()), null);
        if (res == null || res.get("errcode") != null) {
            logger.error("get accessToken error!" + res.getString("errmsg"));
            return null;
        }

        accessToken = res.getString("access_token");
        tokenOutOfTimeTs = TimeTools.currentTS() + res.getLong("expires_in") * 1000;

        return accessToken;
    }

    /**
     * subscribe	用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
     * openid	用户的标识，对当前公众号唯一
     * nickname	用户的昵称
     * sex	用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     * city	用户所在城市
     * country	用户所在国家
     * province	用户所在省份
     * language	用户的语言，简体中文为zh_CN
     * headimgurl	用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
     * subscribe_time	用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
     * unionid	只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。详见：获取用户个人信息（UnionID机制）
     */
    public JSONObject getUserInfo(String openId) {
        JSONObject res = HttpUtils.sendJSONGet(String.format(userInfo, getAccessToken(), openId), null);
        if (res != null && res.get("errcode") == null) {
            return res;
        }

        logger.error("get userInfo error,{},openId:{}", res, openId);
        return null;
    }

    /***
     * 模板消息
     * */

    public void tplMessage(String tpl, JSONObject data, String openId, String toUrl) {
        JSONObject post = newJSONObject();
        post.put("touser", openId);
        post.put("template_id", tpl);
        post.put("url", toUrl == null ? "" : toUrl);
        post.put("data", data);
        String url = String.format(tplMessage, getAccessToken());
        JSONObject res = HttpUtils.sendJSONPost(url, null, post.toJSONString());

        if (res != null && res.getInteger("errcode") == 0) {
            logger.info("send tpl message success! message body: {}", post);
        } else {
            logger.error("send tpl message failed. message body:{},error:{}", post, res);
        }
    }
}
