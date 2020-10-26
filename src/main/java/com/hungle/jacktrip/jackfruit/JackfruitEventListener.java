package com.hungle.jacktrip.jackfruit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import com.hungle.jacktrip.jackfruit.eventbus.EbListener;

public class JackfruitEventListener extends EbListener {
    private static final Logger LOGGER = LogManager.getLogger(JackfruitEventListener.class);

    private final JackFruitEventHandler handler;

    public JackfruitEventListener(JackFruitEventHandler handler) {
        this.handler = handler;
    }

    @Subscribe
    public void jackfruitEvent(JackfruitConnectionEvent event) {
        LOGGER.info("EVENT, event=" + event.toString());
    }

    @Subscribe
    public void handleDeadEvent(DeadEvent deadEvent) {
        LOGGER.info("EVENT, deadEvent=" + deadEvent.toString());
    }

    public JackFruitEventHandler getHandler() {
        return handler;
    }
}