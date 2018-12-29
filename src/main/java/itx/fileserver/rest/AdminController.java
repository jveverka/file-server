package itx.fileserver.rest;

import itx.fileserver.services.FileService;
import itx.fileserver.services.SecurityService;
import itx.fileserver.services.dto.FileStorageInfo;
import itx.fileserver.services.dto.Sessions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/services/admin")
public class AdminController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    private final FileService fileService;
    private final SecurityService securityService;

    @Autowired
    public AdminController(FileService fileService, SecurityService securityService) {
        this.fileService = fileService;
        this.securityService = securityService;
    }

    @GetMapping("/storageinfo")
    public ResponseEntity<FileStorageInfo> getStorageInfo() {
        LOG.info("getStorageInfo:");
        return ResponseEntity.ok().body(fileService.getFileStorageInfo());
    }

    @GetMapping("/sessions")
    public ResponseEntity<Sessions> getSessions() {
        LOG.info("getSessions:");
        return ResponseEntity.ok().body(securityService.getActiveSessions());
    }

}
