package com.hungle.jacktrip.jackfruit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class ParseOutputTest {
    private static final Logger LOGGER = LogManager.getLogger(ParseOutputTest.class);

    @Test
    public void testParseIfConfig() throws IOException {
        try (InputStream stream = this.getClass().getResourceAsStream("ifconfig.txt")) {
            Assert.assertNotNull(stream);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                Assert.assertNotNull(reader);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    LOGGER.info(line);
                }
            }
        }
    }
}
