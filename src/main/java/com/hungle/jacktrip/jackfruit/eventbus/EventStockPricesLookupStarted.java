package com.hungle.jacktrip.jackfruit.eventbus;

import java.util.List;

public class EventStockPricesLookupStarted extends AbstractEbEvent {

    private final List<String> stockSymbols;

    public EventStockPricesLookupStarted(List<String> stockSymbols) {
        this.stockSymbols = stockSymbols;
    }

    public List<String> getStockSymbols() {
        return stockSymbols;
    }
}
