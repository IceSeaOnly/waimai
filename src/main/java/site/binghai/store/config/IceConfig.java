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
}
