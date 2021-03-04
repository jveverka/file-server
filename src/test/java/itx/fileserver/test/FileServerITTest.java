package itx.fileserver.test;

import itx.fileserver.dto.FileStorageInfo;
import itx.fileserver.dto.LoginRequest;
import itx.fileserver.dto.UserData;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FileServerITTest {

    private static final Logger LOG = LoggerFactory.getLogger(FileServerITTest.class);

    private static String jSessionId;

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @Order(1)
    void testStorageInfoNoLogin() {
        LOG.info("Server port {}", port);
        ResponseEntity<FileStorageInfo> responseEntity = restTemplate.getForEntity("/services/admin/storage/info", FileStorageInfo.class);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    @Order(2)
    void testUserLogin() {
        LoginRequest loginRequest = new LoginRequest("master", "secret");
        ResponseEntity<UserData> responseEntity = restTemplate.postForEntity("/services/auth/login", loginRequest, UserData.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        UserData userData = responseEntity.getBody();
        assertEquals("master", userData.getId().getId());
        assertNotNull(userData.getRoles());

        HttpHeaders headers = responseEntity.getHeaders();
        Optional<String> jSessionIdOptional = TestUtils.getJSessionId(headers.getFirst(HttpHeaders.SET_COOKIE));
        assertTrue(jSessionIdOptional.isPresent());
        jSessionId = jSessionIdOptional.get();
        LOG.info("JSESSIONID={}", jSessionId);
    }

    @Test
    @Order(3)
    void testStorageInfo() {
        HttpEntity requestEntity = new HttpEntity(null, TestUtils.createHeaders(jSessionId));
        ResponseEntity<FileStorageInfo> responseEntity = restTemplate.exchange("/services/admin/storage/info", HttpMethod.GET, requestEntity, FileStorageInfo.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @Order(90)
    void testUserLogout() {
        HttpEntity requestEntity = new HttpEntity(null, TestUtils.createHeaders(jSessionId));
        ResponseEntity<FileStorageInfo> responseEntity = restTemplate.exchange("/services/auth/logout", HttpMethod.GET, requestEntity, FileStorageInfo.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

}
