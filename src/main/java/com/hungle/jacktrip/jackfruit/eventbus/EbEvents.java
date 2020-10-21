package com.hungle.jacktrip.jackfruit.eventbus;

import com.google.common.eventbus.EventBus;

public class EbEvents {
    private static final EbEvents INSTANCE = new EbEvents();

    private final EventBus eventBus = new EventBus();
    
    private EbEvents() {
        super();
    }

    public static EbEvents getInstance() {
        return INSTANCE;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
