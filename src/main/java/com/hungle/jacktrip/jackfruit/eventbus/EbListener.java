package com.hungle.jacktrip.jackfruit.eventbus;

public class EbListener {

    public EbListener() {
        super();
    }

    public void register() {
        EbEvents.getInstance().getEventBus().register(this);
    }

}
