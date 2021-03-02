package org.deroesch.demodb;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemodbApplicationTests {

    @Test
    void coverageHelper() {
        new DemodbApplication();

        final String[] args = {};
        DemodbApplication.main(args);
    }

}
