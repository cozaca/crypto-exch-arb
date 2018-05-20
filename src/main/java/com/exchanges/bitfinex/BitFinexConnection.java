package com.exchanges.bitfinex;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;

public final class BitFinexConnection {

    private static final String API_KEY = "";
    private static final String API_SECRET = "";
    private static BitfinexApiBroker bitfinexApiBroker;
    private static boolean usePrivateConnection;

    private BitFinexConnection() {
        usePrivateConnection = !(API_KEY.isEmpty() && API_SECRET.isEmpty());
    }

    public static BitfinexApiBroker getConnection() {
        if (bitfinexApiBroker == null) {
            if (usePrivateConnection) {
                bitfinexApiBroker = new BitfinexApiBroker(API_KEY, API_SECRET);
            } else {
                bitfinexApiBroker = new BitfinexApiBroker();
            }
        }
        return bitfinexApiBroker;
    }
}
