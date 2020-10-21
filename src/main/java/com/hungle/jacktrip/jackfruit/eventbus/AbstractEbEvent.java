package com.hungle.jacktrip.jackfruit.eventbus;

public abstract class AbstractEbEvent implements EbEvent {

    @Override
    public void post() {
        EbEvents.getInstance().getEventBus().post(this);
    }

}
