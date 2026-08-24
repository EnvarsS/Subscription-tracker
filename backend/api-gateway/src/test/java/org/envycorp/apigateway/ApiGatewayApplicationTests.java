package org.envycorp.apigateway;

import org.envycorp.apigateway.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest
class ApiGatewayApplicationTests {
    @MockitoBean
    private JwtService jwtService;

    @Test
    void contextLoads() {
    }

}
