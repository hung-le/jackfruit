package com.hungle.jacktrip.jackfruit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class JackTrippConnectionTest {
    private static final Logger LOGGER = LogManager.getLogger(JackTrippConnectionTest.class);

    @Test
    public void testConnection() {
        try (JackTripConnection jacktrip = new JackTripConnection("192.168.1.90")) {
            System.out.println(jacktrip.runCommand("/usr/sbin/ifconfig -a"));
            System.out.println(jacktrip.runCommand("/usr/bin/netstat -rn"));
            System.out.println(jacktrip.runCommand("/usr/bin/ping -c 3 192.168.1.254"));
            // loopback: 54.193.29.161
            System.out.println(jacktrip.runCommand("/usr/bin/ping -c 3 " + JackFruitMain.DEFAULT_LOOPBACK_TEST_SERVER));
            System.out.println(jacktrip.runCommand("/usr/bin/ps -ef | grep jack"));

        } catch (Exception e) {
            LOGGER.error(e, e);
        }
    }
}