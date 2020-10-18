package com.hungle.jacktrip.jackfruit;

public class ConnectionResult {
    public ConnectionResult(boolean connected, Exception exception) {
        super();
        this.connected = connected;
        this.exception = exception;
    }

    private boolean connected;
    private Exception exception;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}