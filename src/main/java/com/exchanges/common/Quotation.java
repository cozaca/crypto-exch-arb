package com.exchanges.common;

import java.math.BigDecimal;
import java.util.Comparator;

public interface Quotation {
    BigDecimal getPrice();
    BigDecimal getVolume();
    Long getTimeStamp();
}
