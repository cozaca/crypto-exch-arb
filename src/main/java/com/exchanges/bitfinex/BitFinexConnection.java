package com.exchanges.bitfinex;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;

public final class BitFinexConnection {

    private static final String API_KEY = "";
    private static final String API_SECRET ="";
    private static BitfinexApiBroker bitfinexApiBroker;

    private BitFinexConnection() {

    }

    public static BitfinexApiBroker getConnection()
    {
        if (bitfinexApiBroker == null)
        {
            bitfinexApiBroker = new BitfinexApiBroker(API_KEY, API_SECRET);
        }
        return bitfinexApiBroker;
    }
}
