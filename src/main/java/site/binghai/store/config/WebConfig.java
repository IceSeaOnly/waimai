package site.binghai.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import site.binghai.store.inters.UserInterceptor;

/**
 * Created by IceSea on 2018/4/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public UserInterceptor userInter(){
        return new UserInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInter()).addPathPatterns("/user/**");
    }
}
