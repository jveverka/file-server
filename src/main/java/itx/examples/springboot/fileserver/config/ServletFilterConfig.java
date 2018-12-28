package itx.examples.springboot.fileserver.config;

import itx.examples.springboot.fileserver.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletFilterConfig {

    private final SecurityService securityService;

    @Autowired
    public ServletFilterConfig(SecurityService securityService) {
        this.securityService = securityService;
    }


    @Bean
    public FilterRegistrationBean<AuthFilter> loggingFilter(){
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthFilter(securityService));
        registrationBean.addUrlPatterns("/services/files/*");
        return registrationBean;
    }

}
