package itx.fileserver.config;

import itx.fileserver.controler.AdminFilter;
import itx.fileserver.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletFilterConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ServletFilterConfig.class);

    private final SecurityService securityService;

    @Autowired
    public ServletFilterConfig(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Bean
    public FilterRegistrationBean<AdminFilter> loggingFilter() {
        LOG.info("registering admin filter");
        FilterRegistrationBean<AdminFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AdminFilter(securityService));
        registrationBean.addUrlPatterns("/services/admin/*");
        return registrationBean;
    }

}
