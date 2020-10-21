package com.hungle.jacktrip.jackfruit.eventbus;

import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.Subscribe;

public class EbListener1 extends EbListener {
    private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(EbListener1.class);

    private int counter = 0;
    
    @Subscribe
    public void receivedEvent1(EbEvent1 event) {
        LOGGER.info("LISTENER - > receivedEvent1");
        this.counter++;
    }

    public int getCounter() {
        return counter;
    }

}
