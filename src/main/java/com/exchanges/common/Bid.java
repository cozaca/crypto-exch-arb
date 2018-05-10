package com.exchanges.common;

import java.math.BigDecimal;
import java.util.Comparator;

public class Bid  implements Quotation {

    private final BigDecimal price;
    private final BigDecimal volume;
    private final Long timeStamp;

    public static final Comparator<Quotation> comparator = (bid1, bid2) -> {
        int priceComparison = bid1.getPrice().compareTo(bid2.getPrice());
        if (priceComparison < 0 || priceComparison > 0) {
            return priceComparison;
        } else {
            return bid1.getTimeStamp().compareTo(bid2.getTimeStamp());
        }
    };

    public Bid(BigDecimal price, BigDecimal volume, Long timeStamp) {
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
        return "BID: " + getPrice() + " " + getVolume() + " " + getTimeStamp();
    }

}
