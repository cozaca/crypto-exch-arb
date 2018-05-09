package com.exchanges.common;

import java.math.BigDecimal;

public class Ask implements Quotation {

    private final BigDecimal price;
    private final BigDecimal volume;
    private final Long timeStamp;

    public Ask(BigDecimal price, BigDecimal volume, Long timeStamp) {
        this.price = price;
        this.volume = volume;
        this.timeStamp = timeStamp;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public BigDecimal getVolume() {
        return volume;
    }

    @Override
    public Long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return getPrice() + " " + getVolume() + " " + getTimeStamp();
    }
}
