package com.exchanges.common;

import java.math.BigDecimal;
import java.util.Comparator;

public class Ask implements Quotation {

    private final BigDecimal price;
    private final BigDecimal volume;
    private final Long timeStamp;

    public static final Comparator<Quotation> comparator = (ask1, ask2) -> {
        int priceComparison = ask1.getPrice().compareTo(ask2.getPrice());
        if (priceComparison < 0 || priceComparison > 0) {
            return -1 * priceComparison;
        } else {
            return ask1.getTimeStamp().compareTo(ask2.getTimeStamp());
        }
    };

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
        return "ASK: " + getPrice() + " " + getVolume() + " " + getTimeStamp();
    }
}
