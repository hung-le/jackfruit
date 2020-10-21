package com.hungle.jacktrip.jackfruit.eventbus;

import org.junit.Assert;
import org.junit.Test;

public class EbListenerTest {
    @Test
    public void testListener() {
        EbListener1 listener = new EbListener1();
        listener.register();

        EbEvent1 event = new EbEvent1();
        event.post();

        Assert.assertEquals(1, listener.getCounter());
    }
}
