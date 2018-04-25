package com.crypto.marketdata;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;

public class MarketDataTest {

    public static void main(String[] args) {

        BitfinexApiBroker bitfinexApiBroker = new BitfinexApiBroker();
        try {
            bitfinexApiBroker.connect();
        } catch (APIException e) {
            e.printStackTrace();
        }

    }
}
