package com.odc.hub;

import com.odc.hub.config.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class OdcHubBackendApplicationTests {

    @Test
    void contextLoads() {
    }
}
