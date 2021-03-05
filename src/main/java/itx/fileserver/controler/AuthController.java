package itx.fileserver.controler;

import itx.fileserver.services.SecurityService;
import itx.fileserver.dto.LoginRequest;
import itx.fileserver.dto.SessionId;
import itx.fileserver.dto.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping(path = "/services/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final SecurityService securityService;
    private final HttpSession httpSession;

    @Autowired
    public AuthController(SecurityService securityService, HttpSession httpSession) {
        this.securityService = securityService;
        this.httpSession = httpSession;
    }

    @PostMapping("/login")
    public ResponseEntity<UserData> login(@RequestBody LoginRequest loginRequest) {
        LOG.info("login: {} {}", loginRequest.getUserName(), httpSession.getId());
        SessionId sessionId = new SessionId(httpSession.getId());
        Optional<UserData> userData = securityService.authorize(sessionId, loginRequest.getUserName(), loginRequest.getPassword());
        if (userData.isPresent()) {
            return ResponseEntity.ok().body(userData.get());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout() {
        LOG.info("logout: {}", httpSession.getId());
        SessionId sessionId = new SessionId(httpSession.getId());
        securityService.terminateSession(sessionId);
        httpSession.invalidate();
        return ResponseEntity.ok().build();
    }

}
