package com.hungle.jacktrip.jackfruit;

import com.hungle.jacktrip.jackfruit.eventbus.AbstractEbEvent;

public class JackfruitConnectionEvent extends AbstractEbEvent {
    private ConnectionState connectionState;

    public JackfruitConnectionEvent(ConnectionState connectionState) {
        super();
        this.connectionState = connectionState;
    }

    @Override
    public String toString() {
        return "JackfruitEvent [connectionState=" + connectionState + "]";
    }
}