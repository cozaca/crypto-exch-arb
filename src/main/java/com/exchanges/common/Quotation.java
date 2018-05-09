package com.exchanges.common;

import java.math.BigDecimal;

public interface Quotation {

    BigDecimal getPrice();
    BigDecimal getVolume();
    Long getTimeStamp();
}
