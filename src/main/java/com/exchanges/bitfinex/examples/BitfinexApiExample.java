package com.exchanges.bitfinex.examples;

import com.exchanges.bitfinex.BitFinexConnection;
import com.exchanges.bitfinex.BitfinexApi;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.github.jnidzwetzki.bitfinex.v2.entity.Timeframe;

public class BitfinexApiExample {

    public static void main(String[] args)
    {
        BitfinexApi bitfinexApi = new BitfinexApi();
        bitfinexApi.subscribeCandleStickBySymbol(BitfinexCurrencyPair.BTC_EUR, Timeframe.MINUTES_1);
    }
}
