package itx.examples.springboot.fileserver.rest;

import itx.examples.springboot.fileserver.services.SecurityService;
import itx.examples.springboot.fileserver.services.dto.LoginRequest;
import itx.examples.springboot.fileserver.services.dto.UserData;
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

    @Autowired
    private HttpSession httpSession;

    @Autowired
    public AuthController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserData> login(@RequestBody LoginRequest loginRequest) {
        LOG.info("login: {} {}", loginRequest.getUserName(), httpSession.getId());
        Optional<UserData> userData = securityService.authorize(httpSession.getId(), loginRequest.getUserName(), loginRequest.getPassword());
        if (userData.isPresent()) {
            return ResponseEntity.ok().body(userData.get());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/logout")
    public ResponseEntity logout() {
        LOG.info("logout: {}", httpSession.getId());
        securityService.terminateSession(httpSession.getId());
        httpSession.invalidate();
        return ResponseEntity.ok().build();
    }

}
