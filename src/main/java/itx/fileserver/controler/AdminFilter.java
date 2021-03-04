package itx.fileserver.controler;

import itx.fileserver.services.SecurityService;
import itx.fileserver.dto.SessionId;
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

public class AdminFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(AdminFilter.class);

    private final SecurityService securityService;

    public AdminFilter(SecurityService securityService) {
            this.securityService = securityService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        SessionId sessionId = new SessionId(((HttpServletRequest) request).getSession().getId());
        if (securityService.isAuthorizedAdmin(sessionId)) {
            chain.doFilter(request, response);
        } else {
            LOG.info("session {} is not authorized admin session", sessionId);
            ((HttpServletResponse)response).setStatus(HttpStatus.FORBIDDEN.value());
        }
    }
}
