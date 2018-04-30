package site.binghai.store.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by IceSea on 2018/4/23.
 * GitHub: https://github.com/IceSeaOnly
 */
@Component
@ConfigurationProperties(prefix = "ice")
@PropertySource("classpath:application.properties")
@Data
public class IceConfig {
    private boolean enableOpenIdValidate;
    private String validateSeed;

    private String appId;
    private String appSecret;
    private String wmLogin;
    private String server;
    private String payServer;
    private String refundServer;
    private String refundSecret;

    // 退款申请通过模板 OPENTM401747701
    private String refundRequestAcceptedTpl;
    // 外卖订单提醒模板 OPENTM407360983
    private String takeOutOrderTpl;
    // 新订单通知 OPENTM208036452
    private String newOrderNoticeTpl;
    // 退款处理提醒 OPENTM409398653
    private String refuntRequestNoticeTpl;
    // 支付成功通知 OPENTM400231951
    private String paySuccessTpl;
    // 订单取消通知 TM00850
    private String orderCanceledNotice;

}
