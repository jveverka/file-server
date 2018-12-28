package itx.fileserver.config;

import itx.fileserver.services.SecurityService;
import itx.fileserver.services.dto.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class AuthFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class);

    private final SecurityService securityService;

    public AuthFilter(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String sessionId = ((HttpServletRequest) request).getSession().getId();
        Optional<UserData> authorized = securityService.isAuthorized(sessionId);
        if (authorized.isPresent()) {
            chain.doFilter(request, response);
        } else {
            LOG.info("session {} is not authorized", sessionId);
            ((HttpServletResponse)response).setStatus(HttpStatus.FORBIDDEN.value());
        }
    }

}
